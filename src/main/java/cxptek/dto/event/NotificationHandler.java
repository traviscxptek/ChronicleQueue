package cxptek.dto.event;

import cxptek.dto.event.enums.OrderStatus;

public class NotificationHandler extends BaseOrderHandler {

    private static final String NAME = "NotificationHandler";

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        OrderStatus previousStatus = event.getStatus();
        event.setStatus(OrderStatus.NOTIFIED);
        logOrderEvent(NAME, sequence, event, previousStatus);
    }
}
