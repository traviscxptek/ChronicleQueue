package org.main;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import net.openhft.chronicle.bytes.Bytes;
import org.dto.event.LongEvent;
import org.dto.event.LongEventFactory;
import org.dto.event.LongEventHandler;

import java.nio.ByteBuffer;

public class DistributedEvent {

    public static void main(String[] args) throws Exception {
        int bufferSize = 1024;

        Disruptor<LongEvent> disruptor = new Disruptor<>(new LongEventFactory(),
                bufferSize,
                DaemonThreadFactory.INSTANCE,
                ProducerType.SINGLE,
                new BlockingWaitStrategy());

        disruptor.handleEventsWith(new LongEventHandler("Consumer 1"));
        disruptor.handleEventsWith(new LongEventHandler("Consumer 2"))
                .then(new LongEventHandler("Then Consumer 2"));
        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; l < 1_000_000L; l++) {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer)
                    -> event.setId(buffer.getLong(0)), bb);
        }
        Thread.sleep(5000);
        Runtime.getRuntime().addShutdownHook(new Thread(disruptor::shutdown));
    }
}
