package cxptek.service;


import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public interface LocalQueueService {

    ConcurrentHashMap<String, Queue<byte[]>> initializeQueues(String... queueNames);

    Queue<byte[]> addQueue(String queueName);

    boolean removeQueue(String queueName);
}
