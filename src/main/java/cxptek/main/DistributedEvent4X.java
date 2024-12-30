package cxptek.main;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.BatchEventProcessorBuilder;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import cxptek.dto.event.BusinessOrderHandler;
import cxptek.dto.event.NotificationHandler;
import cxptek.dto.event.OrderEvent;
import cxptek.dto.event.OrderEventFactory;
import cxptek.dto.event.OrderDataHandler;

import java.util.concurrent.Executors;

public class DistributedEvent4X {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        int bufferSize = 1024;
        Disruptor<OrderEvent> disruptor = new Disruptor<>(new OrderEventFactory(),
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BusySpinWaitStrategy());

        EventHandler<OrderEvent> businessHandler = new BusinessOrderHandler();
        EventHandler<OrderEvent> persistDataHandler = new OrderDataHandler();
        EventHandler<OrderEvent> notificationHandler = new NotificationHandler();

        disruptor.handleEventsWith(businessHandler)
                .then(persistDataHandler)
                .then(notificationHandler);


        RingBuffer<OrderEvent> ringBuffer = RingBuffer.create(ProducerType.MULTI, new OrderEventFactory(), bufferSize, new BusySpinWaitStrategy());
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        BatchEventProcessor<OrderEvent> businessProcessor = new BatchEventProcessorBuilder().build(ringBuffer, sequenceBarrier, businessHandler);
        Sequence businessSequence = businessProcessor.getSequence();

        //Then processor is after businessProcessor
        SequenceBarrier persistDataSequenceBarrier = ringBuffer.newBarrier(businessSequence);
        BatchEventProcessor<OrderEvent> persistDataProcessor = new BatchEventProcessorBuilder().build(ringBuffer, persistDataSequenceBarrier, persistDataHandler);
        Sequence persistDataSequence = persistDataProcessor.getSequence();


        Executors.defaultThreadFactory().newThread(businessProcessor).start();
        Executors.defaultThreadFactory().newThread(persistDataProcessor).start();


        for (long l = 0; l < 10; l++) {
            long sequence = ringBuffer.next();
            OrderEvent event = ringBuffer.get(sequence);
            event.setId(l);
            event.setMessage("Event " + l);
            ringBuffer.publish(sequence);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("events consumer time: " + (System.currentTimeMillis() - startTime));
        }));
    }
}
