package cxptek.service.iml;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import cxptek.dto.enums.OrderStatus;
import cxptek.dto.event.OrderEvent;
import cxptek.dto.event.OrderEventFactory;
import cxptek.service.DisruptorService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;

import static cxptek.utils.Constants.BUFFER_SIZE;

@Slf4j
public class DisruptorServiceImpl implements DisruptorService {


    @Override
    public Disruptor<OrderEvent> createDefaultOrderDisruptor() {
        return new Disruptor<>(new OrderEventFactory(),
                BUFFER_SIZE,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy());
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
//            int finalI = i;
//            ringBuffer.publishEvent((orderEvent, sequence) -> {
//                orderEvent.setId((long) finalI);
//                orderEvent.setPayload("Event " + finalI);
//                orderEvent.setStatus(OrderStatus.ACTIVE);
//            });
        }
    }

    @Override
    public void publishSimulatorOrderEvents(List<Disruptor<OrderEvent>> disruptors, long batchSize) {
        int disruptorSize = disruptors.size();
        for (long i = 0; i < batchSize; i++) {
            int index = (int) i % disruptorSize;
            long finalI = i;
            Disruptor<OrderEvent> disruptor = disruptors.get(index);
            disruptor.publishEvent((order, sequence) -> {
                System.out.printf("Publish event for sequence %d\n", finalI);
                order.setId( finalI);
                order.setPayload("Event " + finalI);
                order.setStatus(OrderStatus.ACTIVE);
            });
        }
    }
}
