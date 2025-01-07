package cxptek.service;

import cxptek.dto.event.OrderEvent;

import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface RingExecutorService {

    void clearRingExecutors();

    void addRingExecutor(String executorName);

    void removeRingExecutor(String executorName);

    Future<OrderEvent> submitTask(OrderEvent orderEvent,
                                  Consumer<OrderEvent> consumer);
}
