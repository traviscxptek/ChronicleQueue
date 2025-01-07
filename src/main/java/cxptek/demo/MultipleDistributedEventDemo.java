package cxptek.demo;

import com.google.inject.Inject;
import com.lmax.disruptor.dsl.Disruptor;
import cxptek.disruption.BusinessOrderHandler;
import cxptek.dto.enums.OrderStatus;
import cxptek.dto.event.OrderEvent;
import cxptek.service.DisruptorService;
import cxptek.service.RingDisruptorService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static cxptek.utils.Constants.BATCH_SIZE;
import static cxptek.utils.Constants.BUFFER_SIZE;
import static cxptek.utils.Constants.NUMBER_OF_DISRUPTOR;

@Slf4j
public class MultipleDistributedEventDemo {

    private final DisruptorService disruptorService;
    private final RingDisruptorService ringDisruptorService;

    @Inject
    public MultipleDistributedEventDemo(DisruptorService disruptorService,
                                        RingDisruptorService ringDisruptorService) {
        this.disruptorService = disruptorService;
        this.ringDisruptorService = ringDisruptorService;
    }

    public void start() {
        initializeDisruptors();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            OrderEvent orderEvent = OrderEvent.builder()
                    .id((long) i)
                    .payload("Event " + i)
                    .status(OrderStatus.ACTIVE)
                    .build();
            orderEvent.setId((long) i);
            orderEvent.setPayload("Event " + i);
            orderEvent.setStatus(OrderStatus.ACTIVE);
            ringDisruptorService.publishOrder(orderEvent);
        }
    }

    private void initializeDisruptors() {
        for (int i = 0; i < NUMBER_OF_DISRUPTOR; i++) {
            Disruptor<OrderEvent> disruptor = disruptorService.createDefaultOrderDisruptor();
            ringDisruptorService.addDisruptor("disruptor-" + i, disruptor);
            BusinessOrderHandler orderHandler = new BusinessOrderHandler("BusinessOrderHandler-" + i);
            disruptor.handleEventsWith(orderHandler);
            disruptor.start();
        }
    }
}
