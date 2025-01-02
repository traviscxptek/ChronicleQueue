package cxptek.demo;

import com.google.inject.Inject;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.Sequencer;
import com.lmax.disruptor.dsl.ExceptionHandlerWrapper;
import com.lmax.disruptor.dsl.ProducerType;
import cxptek.disruption.BusinessOrderHandler;
import cxptek.disruption.processor.CxptekEventProcessor;
import cxptek.service.DisruptorService;
import cxptek.dto.event.OrderEvent;
import cxptek.dto.event.OrderEventFactory;
import cxptek.disruption.OrderDataHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class WorkerPoolEventDemo {

    private final DisruptorService disruptorService;

    @Inject
    public WorkerPoolEventDemo(DisruptorService disruptorService) {
        this.disruptorService = disruptorService;
    }

    public void start() {
        long startTime = System.currentTimeMillis();
        int bufferSize = 1024;

        RingBuffer<OrderEvent> ringBuffer = RingBuffer.create(ProducerType.MULTI,
                new OrderEventFactory(),
                bufferSize,
                new BlockingWaitStrategy());

        SequenceBarrier businessSequenceBarrier = ringBuffer.newBarrier();
        final Sequence businessWorkerSequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
        EventHandler<OrderEvent> businessHandler = new BusinessOrderHandler();
        EventHandler<OrderEvent> businessHandler2 = new BusinessOrderHandler("BusinessOrderHandler v2");
        List<CxptekEventProcessor<OrderEvent>> businessProcessors = createProcessors(ringBuffer,
                businessSequenceBarrier,
                businessWorkerSequence,
                List.of(businessHandler, businessHandler2));
        businessWorkerSequence.set(ringBuffer.getCursor());

        //persist data after handled logic business
        Sequence[] businessSequences = businessProcessors.stream().map(CxptekEventProcessor::getSequence)
                .toArray(Sequence[]::new);
        SequenceBarrier persistSequenceBarrier = ringBuffer.newBarrier(businessSequences);
        final Sequence persistDataWorkerSequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
        List<CxptekEventProcessor<OrderEvent>> persistDataProcessor = createProcessors(ringBuffer,
                persistSequenceBarrier,
                persistDataWorkerSequence,
                List.of(new OrderDataHandler(), new OrderDataHandler("OrderDataHandler v2")));

        try (ExecutorService businessExecutor = Executors.newFixedThreadPool(businessProcessors.size());
             ExecutorService persistDataExecutor = Executors.newFixedThreadPool(persistDataProcessor.size())) {
            businessProcessors.forEach(processor -> {
                processor.getSequence().set(ringBuffer.getCursor());
                businessExecutor.submit(processor);
            });
            persistDataProcessor.forEach(processor -> {
                processor.getSequence().set(ringBuffer.getCursor());
                persistDataExecutor.submit(processor);
            });
            disruptorService.publishSimulatorOrderEvents(ringBuffer, 10);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Events consumer time: " + (System.currentTimeMillis() - startTime));
        }));
    }


    private List<CxptekEventProcessor<OrderEvent>> createProcessors(RingBuffer<OrderEvent> ringBuffer,
                                                                    SequenceBarrier sequenceBarrier,
                                                                    Sequence workerSequence,
                                                                    List<EventHandler<OrderEvent>> handlers) {
        ExceptionHandler<OrderEvent> exceptionHandler = new ExceptionHandlerWrapper<>();
        return handlers.stream().map(handler ->
                new CxptekEventProcessor<>(ringBuffer,
                        sequenceBarrier,
                        handler,
                        exceptionHandler,
                        workerSequence)
        ).toList();
    }
}
