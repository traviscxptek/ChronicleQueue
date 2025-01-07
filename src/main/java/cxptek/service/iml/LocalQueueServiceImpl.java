package cxptek.service.iml;

import cxptek.dto.event.LocalQueueEvent;
import cxptek.dto.event.OrderEvent;
import cxptek.service.LocalQueueService;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class LocalQueueServiceImpl implements LocalQueueService {

    private final ConcurrentHashMap<String, Queue<LocalQueueEvent>> nameQueueMap;

    public LocalQueueServiceImpl() {
        nameQueueMap = new ConcurrentHashMap<>();
    }

    @Override
    public ConcurrentHashMap<String, Queue<LocalQueueEvent>> initializeQueues(String... queueNames) {
        nameQueueMap.clear();
        for (String queueName : queueNames) {
            nameQueueMap.put(queueName, new LinkedList<>());
        }
        return nameQueueMap;
    }

    @Override
    public void addQueue(String queueName) {
        nameQueueMap.put(queueName, new LinkedList<>());
    }

    @Override
    public void removeQueue(String queueName) {
        nameQueueMap.remove(queueName);
    }

    @Override
    public boolean existsQueue(String queueName) {
        return !nameQueueMap.containsKey(queueName);
    }

    @Override
    public void publishOrderEvent(OrderEvent orderEvent,
                                  CountDownLatch countDownLatch,
                                  String queueName) {
        if (existsQueue(queueName)) {
            throw new RuntimeException("Queue " + queueName + " does not exist");
        }
        nameQueueMap.get(queueName).add(LocalQueueEvent.builder()
                .order(orderEvent)
                .countDownLatch(countDownLatch)
                .build());
    }

    @Override
    public void startPollQueue(String queueName, Consumer<LocalQueueEvent> consumer) {
        if (!nameQueueMap.containsKey(queueName)) {
            return;
        }
        new Thread(() -> {
            Queue<LocalQueueEvent> queue = nameQueueMap.get(queueName);
            while (true) {
                LocalQueueEvent localQueueEvent = queue.poll();
                if (Objects.nonNull(localQueueEvent)) {
                    consumer.accept(localQueueEvent);
                    localQueueEvent.getCountDownLatch().countDown();
                }
            }
        }).start();
    }
}
