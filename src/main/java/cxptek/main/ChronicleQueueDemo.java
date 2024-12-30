package cxptek.main;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;

import java.util.concurrent.atomic.AtomicLong;

public class ChronicleQueueDemo {
    public static void main(String[] args) {
        int testRecord = 5000000;
        final Bytes<byte[]> byteBuffer = Bytes.allocateElasticOnHeap(4 * 1024);
        try (ChronicleQueue q = ChronicleQueue.singleBuilder("queue1").build()) {
            ExcerptAppender appender = q.createAppender();
            AtomicLong atomicLong = new AtomicLong();
            PayloadOrderCOmmand payloadOrderCOmmand = new PayloadOrderCOmmand();
            long s = System.currentTimeMillis();
            for (int i = 0; i < testRecord; i++) {
                byteBuffer.clear();
                payloadOrderCOmmand.id1 = atomicLong.incrementAndGet();
                payloadOrderCOmmand.writeMarshallable(byteBuffer);
                appender.writeBytes(byteBuffer);
            }
            System.out.println(System.currentTimeMillis() - s + " test");
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(ChronicleQueueDemo::consumer).start();
    }

    private static void consumer() {
        ChronicleQueue q = ChronicleQueue.singleBuilder("queue1").build();
        ExcerptTailer tailer  = q.createTailer();

        while (tailer.readBytes(bytes -> {
            PayloadOrderCOmmand payloadOrderCOmmand = new PayloadOrderCOmmand();
            payloadOrderCOmmand.readMarshallable(bytes);
//            System.out.println(payloadOrderCOmmand.id1);
        }));
    }
}