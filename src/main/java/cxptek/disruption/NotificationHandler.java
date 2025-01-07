package cxptek.disruption;

import cxptek.dto.event.OrderEvent;
import cxptek.dto.enums.OrderStatus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class NotificationHandler extends BaseOrderHandler {

    private static final String NAME = "NotificationHandler";

    @Override
    public void handler(OrderEvent event, long sequence) {
        try {
            Thread.sleep(SIMULATION_TIME);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        OrderStatus previousStatus = event.getStatus();
        event.setStatus(OrderStatus.NOTIFIED);
        logOrderEvent(NAME, sequence, event, previousStatus);
    }

    @Override
    public void handlerWithRingExecutor(OrderEvent event, long sequence)
            throws ExecutionException, InterruptedException, TimeoutException {
        Consumer<OrderEvent> orderConsumer = order -> handler(order, sequence);
        ringExecutorService.submitTask(event, orderConsumer)
                .get(FUTURE_SECONDS_TIMEOUT, TimeUnit.SECONDS);
    }
}
