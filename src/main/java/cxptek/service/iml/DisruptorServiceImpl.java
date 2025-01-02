package cxptek.service.iml;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import cxptek.dto.enums.OrderStatus;
import cxptek.dto.event.OrderEvent;
import cxptek.dto.event.OrderEventFactory;
import cxptek.service.DisruptorService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;

@Slf4j
public class DisruptorServiceImpl implements DisruptorService {

    private static final int BUFFER_SIZE = 1024;

    @Override
    public Disruptor<OrderEvent> createDefaultOrderDisruptor() {
        return new Disruptor<>(new OrderEventFactory(),
                BUFFER_SIZE,
                Executors.defaultThreadFactory());
    }

    @Override
    public void publishSimulatorOrderEvents(RingBuffer<OrderEvent> ringBuffer, int batchSize) {
        for (int i = 0; i < batchSize; i++) {
            long sequence = ringBuffer.next();
            OrderEvent orderEvent = ringBuffer.get(sequence);
            orderEvent.setId((long) i);
            orderEvent.setPayload("Event " + i);
            orderEvent.setStatus(OrderStatus.ACTIVE);
            ringBuffer.publish(sequence);
        }
    }
}
