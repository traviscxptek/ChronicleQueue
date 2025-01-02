package cxptek.service;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import cxptek.dto.event.OrderEvent;

public interface DisruptorService {

    Disruptor<OrderEvent> createDefaultOrderDisruptor();

    void publishSimulatorOrderEvents(RingBuffer<OrderEvent> ringBuffer, int batchSize);
}
