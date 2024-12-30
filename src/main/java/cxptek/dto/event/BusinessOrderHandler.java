package cxptek.dto.event;

import cxptek.dto.event.enums.OrderStatus;

public class BusinessOrderHandler extends AbstractOrderHandler {

    private static final String NAME = "BusinessOrderHandler";

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        event.setStatus(OrderStatus.COMPLETED);
        logOrderEvent(NAME, sequence, event);
    }
}
