package cxptek.disruption;

import com.lmax.disruptor.RingBuffer;
import cxptek.dto.event.OrderEvent;
import cxptek.dto.enums.OrderStatus;
import lombok.Setter;

import java.util.Objects;

@Setter
public class DisruptorOrderHandler extends BaseOrderHandler {

    private RingBuffer<OrderEvent> ringBuffer;

    private String name = "DisruptorOrderHandler";

    public DisruptorOrderHandler(String handlerName) {
        this.name = handlerName;
    }

    public DisruptorOrderHandler() {
    }

    public DisruptorOrderHandler(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
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
        if (Objects.nonNull(ringBuffer)) {
            long seq = ringBuffer.next();
            OrderEvent orderEvent = ringBuffer.get(seq);
            ringBuffer.publish(seq);
        }
        logOrderEvent(name, sequence, event, previousStatus);
    }
}
