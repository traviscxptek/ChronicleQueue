package cxptek.disruption;

import cxptek.dto.event.OrderEvent;
import cxptek.dto.enums.OrderStatus;
import cxptek.service.RingExecutorService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class OrderDataHandler extends BaseOrderHandler {

    private String name = "OrderDataHandler";

    public OrderDataHandler() {
    }

    public OrderDataHandler(String name) {
        this.name = name;
    }

    public OrderDataHandler(RingExecutorService ringExecutorService) {
        this.ringExecutorService = ringExecutorService;
    }

    public OrderDataHandler(String name, RingExecutorService ringExecutorService) {
        this.name = name;
        this.ringExecutorService = ringExecutorService;
    }

    @Override
    public void handler(OrderEvent event, long sequence) {
        try {
            Thread.sleep(SIMULATION_TIME);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        OrderStatus previousStatus = event.getStatus();
        event.setStatus(OrderStatus.PERSISTED);
        logOrderEvent(name, sequence, event, previousStatus);
    }

    @Override
    public void handlerWithRingExecutor(OrderEvent event, long sequence)
            throws ExecutionException, InterruptedException, TimeoutException {
        Consumer<OrderEvent> orderConsumer = order -> handler(order, sequence);
        ringExecutorService.submitTask(event, orderConsumer)
                .get(FUTURE_SECONDS_TIMEOUT, TimeUnit.SECONDS);
    }
}
