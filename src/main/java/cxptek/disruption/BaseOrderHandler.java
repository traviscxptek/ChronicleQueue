package cxptek.disruption;

import com.lmax.disruptor.EventHandler;
import cxptek.dto.event.OrderEvent;
import cxptek.dto.enums.OrderStatus;

public abstract class BaseOrderHandler implements EventHandler<OrderEvent> {

    protected void logOrderEvent(String handlerName,
                                 long sequence,
                                 OrderEvent event,
                                 OrderStatus previousStatus) {
        System.out.println("Processor: " + handlerName
                + " Thread: " + Thread.currentThread().threadId()
                + " previousStatus: " + previousStatus
                + " Event: " + event
                + " Sequence: " + sequence);
        System.out.flush();
    }
}
