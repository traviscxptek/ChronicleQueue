package cxptek.service;

import cxptek.dto.event.OrderEvent;

public interface RingQueueService {

    void addQueue(String queueName);

    void removeQueue(String queueName);

    void publishQueue(OrderEvent order);
}
