package cxptek.service;


import cxptek.dto.event.LocalQueueEvent;
import cxptek.dto.event.OrderEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public interface LocalQueueService {

    ConcurrentHashMap<String, Queue<LocalQueueEvent>> initializeQueues(String... queueNames);

    void addQueue(String queueName);

    void removeQueue(String queueName);

    boolean existsQueue(String queueName);

    void publishOrderEvent(OrderEvent orderEvent, CountDownLatch countDownLatch, String queueName);

    void startPollQueue(String queueName, Consumer<LocalQueueEvent> consumer);

}
