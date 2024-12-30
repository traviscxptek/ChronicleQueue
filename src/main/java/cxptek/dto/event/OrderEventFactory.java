package cxptek.dto.event;

import com.lmax.disruptor.EventFactory;
import cxptek.dto.event.enums.OrderStatus;

public class OrderEventFactory implements EventFactory<OrderEvent> {
    @Override
    public OrderEvent newInstance() {
        return OrderEvent.builder()
                .message("Order Event")
                .status(OrderStatus.ACTIVE)
                .build();
    }
}
