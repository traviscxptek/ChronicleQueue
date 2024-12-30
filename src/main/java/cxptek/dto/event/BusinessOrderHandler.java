package cxptek.dto.event;

import cxptek.dto.event.enums.OrderStatus;

public class BusinessOrderHandler extends BaseOrderHandler {

    private String name = "BusinessOrderHandler";

    public BusinessOrderHandler(String handlerName) {
        this.name = handlerName;
    }

    public BusinessOrderHandler() {
    }

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        OrderStatus previousStatus = event.getStatus();
        event.setStatus(OrderStatus.COMPLETED);
        logOrderEvent(name, sequence, event, previousStatus);
    }
}
