package cxptek.dto.event;

import com.lmax.disruptor.EventHandler;

public abstract class AbstractOrderHandler implements EventHandler<OrderEvent> {

    protected void logOrderEvent(String handlerName,
                                 long sequence,
                                 OrderEvent event) {
        System.out.println("Processor: " + handlerName
                + " Thread: " + Thread.currentThread().threadId()
                + " Event: " + event
                + " Sequence: " + sequence);
        System.out.flush();
    }
}
