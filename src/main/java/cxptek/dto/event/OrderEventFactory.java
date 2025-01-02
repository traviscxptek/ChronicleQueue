package cxptek.dto.event;

import com.lmax.disruptor.EventFactory;
import cxptek.dto.enums.OrderStatus;

public class OrderEventFactory implements EventFactory<OrderEvent> {
    @Override
    public OrderEvent newInstance() {
        return OrderEvent.builder()
                .payload("Order Event")
                .status(OrderStatus.ACTIVE)
                .build();
    }
}
