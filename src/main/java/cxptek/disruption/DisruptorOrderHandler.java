package cxptek.disruption;

import com.lmax.disruptor.RingBuffer;
import cxptek.dto.event.OrderEvent;
import cxptek.dto.enums.OrderStatus;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

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
    public void handlerWithRingExecutor(OrderEvent event, long sequence) throws ExecutionException, InterruptedException, TimeoutException {
        Consumer<OrderEvent> orderConsumer = order -> handler(order, sequence);
        ringExecutorService.submitTask(event, orderConsumer)
                .get(FUTURE_SECONDS_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public void handler(OrderEvent event, long sequence) {
        try {
            Thread.sleep(SIMULATION_TIME);
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
