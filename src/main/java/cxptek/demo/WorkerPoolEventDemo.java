package cxptek.demo;

import com.google.inject.Inject;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.Sequencer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ExceptionHandlerWrapper;
import com.lmax.disruptor.dsl.ProducerType;
import cxptek.disruption.BaseOrderHandler;
import cxptek.disruption.BusinessOrderHandler;
import cxptek.disruption.processor.CxptekEventProcessor;
import cxptek.service.DisruptorService;
import cxptek.dto.event.OrderEvent;
import cxptek.dto.event.OrderEventFactory;
import cxptek.disruption.OrderDataHandler;
import cxptek.service.RingExecutorService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cxptek.utils.Constants.BUFFER_SIZE;


@Slf4j
public class WorkerPoolEventDemo {

    private final DisruptorService disruptorService;
    private final RingExecutorService ringExecutorService;

    @Inject
    public WorkerPoolEventDemo(DisruptorService disruptorService,
                               RingExecutorService ringExecutorService) {
        this.disruptorService = disruptorService;
        this.ringExecutorService = ringExecutorService;
    }

    public void start() {
        long startTime = System.currentTimeMillis();

        RingBuffer<OrderEvent> ringBuffer = RingBuffer.create(ProducerType.SINGLE,
                new OrderEventFactory(),
                BUFFER_SIZE,
                new BlockingWaitStrategy());

        SequenceBarrier businessSequenceBarrier = ringBuffer.newBarrier();
        final Sequence businessWorkerSequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
        BaseOrderHandler businessHandler = new BusinessOrderHandler();
        businessHandler.setRingExecutorService(ringExecutorService);
        ringExecutorService.addRingExecutor("BusinessHandler");
        BaseOrderHandler businessHandler2 = new BusinessOrderHandler("BusinessOrderHandler v2");
        businessHandler2.setRingExecutorService(ringExecutorService);
        ringExecutorService.addRingExecutor("BusinessOrderHandlerV2");

        List<CxptekEventProcessor<OrderEvent>> businessProcessors = createProcessors(ringBuffer,
                businessSequenceBarrier,
                businessWorkerSequence,
                List.of(businessHandler));
        businessWorkerSequence.set(ringBuffer.getCursor());

        //persist data after handled logic business
        Sequence[] businessSequences = businessProcessors.stream().map(CxptekEventProcessor::getSequence)
                .toArray(Sequence[]::new);
        SequenceBarrier persistSequenceBarrier = ringBuffer.newBarrier(businessSequences);
        final Sequence persistDataWorkerSequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
        BaseOrderHandler orderDataHandler = new OrderDataHandler();
        orderDataHandler.setRingExecutorService(ringExecutorService);
        ringExecutorService.addRingExecutor("OrderDataHandler");
        BaseOrderHandler orderDataHandler2 = new OrderDataHandler("OrderDataHandler v2");
        ringExecutorService.addRingExecutor("OrderDataHandlerV2");
        orderDataHandler2.setRingExecutorService(ringExecutorService);
        List<CxptekEventProcessor<OrderEvent>> persistDataProcessor = createProcessors(ringBuffer,
                persistSequenceBarrier,
                persistDataWorkerSequence,
                List.of(orderDataHandler, orderDataHandler2));

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
            disruptorService.publishSimulatorOrderEvents(ringBuffer, 100);
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
