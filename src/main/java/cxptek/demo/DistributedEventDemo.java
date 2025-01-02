package cxptek.demo;

import com.google.inject.Inject;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import cxptek.disruption.DisruptorOrderHandler;
import cxptek.service.DisruptorService;
import cxptek.disruption.NotificationHandler;
import cxptek.dto.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DistributedEventDemo {

    private final DisruptorService disruptorService;

    @Inject
    public DistributedEventDemo(DisruptorService disruptorService) {
        this.disruptorService = disruptorService;
    }

    public void start() {
        long startTime = System.currentTimeMillis();

        Disruptor<OrderEvent> disruptor = disruptorService.createDefaultOrderDisruptor();
        Disruptor<OrderEvent> notiDisruptor = disruptorService.createDefaultOrderDisruptor();


        DisruptorOrderHandler disruptorOrderHandler = new DisruptorOrderHandler();
        disruptor.handleEventsWith(disruptorOrderHandler);


        NotificationHandler notificationHandler = new NotificationHandler();
        notiDisruptor.handleEventsWith(notificationHandler);

        RingBuffer<OrderEvent> ringBuffer = disruptor.start();
        RingBuffer<OrderEvent> notifiRingBuffer = notiDisruptor.start();
        disruptorOrderHandler.setRingBuffer(notifiRingBuffer);

        disruptorService.publishSimulatorOrderEvents(ringBuffer, 10);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            disruptor.shutdown();
            System.out.println("Events consumer time: " + (System.currentTimeMillis() - startTime));
        }));
    }
}
