package cxptek.disruption;

import cxptek.dto.event.OrderEvent;
import cxptek.dto.enums.OrderStatus;
import cxptek.service.RingExecutorService;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import static cxptek.utils.Constants.BUFFER_SIZE;

public class BusinessOrderHandler extends BaseOrderHandler {

    private String name = "BusinessOrderHandler";

    public BusinessOrderHandler() {
    }

    public BusinessOrderHandler(String name) {
        this.name = name;
    }

    public BusinessOrderHandler(String handlerName,
                                RingExecutorService ringExecutorService) {
        this.name = handlerName;
        this.ringExecutorService = ringExecutorService;
    }

    public BusinessOrderHandler(RingExecutorService ringExecutorService) {
        this.ringExecutorService = ringExecutorService;
    }

    @Override
    public void handlerWithRingExecutor(OrderEvent orderEvent, long sequence)
            throws ExecutionException, InterruptedException, TimeoutException {
//        System.out.printf("Sequence executor: %d \n", sequence);
        Consumer<OrderEvent> orderConsumer = order -> handler(order, sequence);
        ringExecutorService.submitTask(orderEvent, orderConsumer)
                .get(FUTURE_SECONDS_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public void handler(OrderEvent event, long sequence) {
//        System.out.printf("Sequence consumer: %d \n", sequence);
//        if (sequence % BUFFER_SIZE == 0) {
//            try {
//                Thread.sleep(SIMULATION_TIME);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
        OrderStatus previousStatus = event.getStatus();
        event.setStatus(OrderStatus.COMPLETED);
        logOrderEvent(name, sequence, event, previousStatus);
    }
}
