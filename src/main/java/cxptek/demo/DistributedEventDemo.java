package cxptek.demo;

import com.google.inject.Inject;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import cxptek.disruption.BaseOrderHandler;
import cxptek.disruption.BusinessOrderHandler;
import cxptek.disruption.DisruptorOrderHandler;
import cxptek.dto.event.OrderEventFactory;
import cxptek.service.DisruptorService;
import cxptek.disruption.NotificationHandler;
import cxptek.dto.event.OrderEvent;
import cxptek.service.RingExecutorService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;

import static cxptek.utils.Constants.BUFFER_SIZE;

@Slf4j
public class DistributedEventDemo {

    private final DisruptorService disruptorService;
    private final RingExecutorService ringExecutorService;

    @Inject
    public DistributedEventDemo(DisruptorService disruptorService,
                                RingExecutorService ringExecutorService) {
        this.disruptorService = disruptorService;
        this.ringExecutorService = ringExecutorService;
    }

    public void start() {
        long startTime = System.currentTimeMillis();

        Disruptor<OrderEvent> disruptor =new Disruptor<>(new OrderEventFactory(),
                BUFFER_SIZE,
                Executors.defaultThreadFactory(),
                ProducerType.MULTI,
                new BlockingWaitStrategy());


        BaseOrderHandler disruptorOrderHandler = new BusinessOrderHandler("BusinessOrderHandler");
        disruptor.handleEventsWith(disruptorOrderHandler);

        RingBuffer<OrderEvent> ringBuffer = disruptor.start();
        disruptorService.publishSimulatorOrderEvents(ringBuffer, 200);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            disruptor.shutdown();
            System.out.println("Events consumer time: " + (System.currentTimeMillis() - startTime));
        }));
    }
}
