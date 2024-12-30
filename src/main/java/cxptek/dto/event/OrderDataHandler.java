package cxptek.dto.event;

import cxptek.dto.event.enums.OrderStatus;

public class OrderDataHandler extends AbstractOrderHandler {

    private static final String NAME = "OrderDataHandler";

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        event.setStatus(OrderStatus.PERSISTED);
        logOrderEvent(NAME, sequence, event);
    }
}
