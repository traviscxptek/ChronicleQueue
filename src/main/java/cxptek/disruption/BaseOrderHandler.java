package cxptek.disruption;

import com.lmax.disruptor.EventHandler;
import cxptek.dto.event.OrderEvent;
import cxptek.dto.enums.OrderStatus;
import cxptek.service.LockSimulatorService;
import cxptek.service.RingExecutorService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static cxptek.utils.Constants.BATCH_SIZE;
import static cxptek.utils.Constants.NUMBER_OF_DISRUPTOR;

@Setter
@Slf4j
public abstract class BaseOrderHandler implements EventHandler<OrderEvent> {

    protected static final Long SIMULATION_TIME = 1000L;
    protected static final Long FUTURE_SECONDS_TIMEOUT = 30L;
    protected static final Long FLAG_NUMBER = BATCH_SIZE / NUMBER_OF_DISRUPTOR;

    protected RingExecutorService ringExecutorService;
    protected LockSimulatorService lockSimulatorService;
    protected HashMap<Long, OrderEvent> processedOrders = new HashMap<>();

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

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        if (processedOrders.containsKey(event.getId())) {
            System.out.println("Order already processed: " + event.getId());
        }
        processedOrders.put(event.getId(), event);
//        if (sequence > -1) {
//            System.out.printf("Order processed:  %d sequence %d\n", event.getId(), sequence);
//        }
        if (Objects.nonNull(ringExecutorService)) {
            try {
                handlerWithRingExecutor(event, sequence);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            return;
        }
        handler(event, sequence);
    }

    public abstract void handler(OrderEvent event, long sequence);

    public abstract void handlerWithRingExecutor(OrderEvent event, long sequence)
            throws ExecutionException, InterruptedException, TimeoutException;
}
