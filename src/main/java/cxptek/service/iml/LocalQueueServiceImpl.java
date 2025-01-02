package cxptek.service.iml;

import cxptek.service.LocalQueueService;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class LocalQueueServiceImpl implements LocalQueueService {

    private final ConcurrentHashMap<String, Queue<byte[]>> nameQueueMap;

    public LocalQueueServiceImpl() {
        nameQueueMap = new ConcurrentHashMap<>();
    }

    @Override
    public ConcurrentHashMap<String, Queue<byte[]>> initializeQueues(String... queueNames) {
        nameQueueMap.clear();
        for (String queueName : queueNames) {
            nameQueueMap.put(queueName, new LinkedList<>());
        }
        return nameQueueMap;
    }

    @Override
    public Queue<byte[]> addQueue(String queueName) {
        return nameQueueMap.put(queueName, new LinkedList<>());
    }

    @Override
    public boolean removeQueue(String queueName) {
        return nameQueueMap.remove(queueName) != null;
    }
}
