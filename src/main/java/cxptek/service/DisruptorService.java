package cxptek.service;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import cxptek.dto.event.OrderEvent;

import java.util.List;

public interface DisruptorService {

    Disruptor<OrderEvent> createDefaultOrderDisruptor();

    void publishSimulatorOrderEvents(RingBuffer<OrderEvent> ringBuffer, int batchSize);

    void publishSimulatorOrderEvents(List<Disruptor<OrderEvent>> disruptors, long batchSize);
}
