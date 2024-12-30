package cxptek.dto.event;

import cxptek.dto.event.enums.OrderStatus;

public class OrderDataHandler extends BaseOrderHandler {

    private String name = "OrderDataHandler";


    public OrderDataHandler() {
    }

    public OrderDataHandler(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        OrderStatus previousStatus = event.getStatus();
        event.setStatus(OrderStatus.PERSISTED);
        logOrderEvent(name, sequence, event, previousStatus);
    }
}
