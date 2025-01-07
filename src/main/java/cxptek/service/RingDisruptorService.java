package cxptek.service;

import com.lmax.disruptor.dsl.Disruptor;
import cxptek.dto.event.OrderEvent;

import java.util.List;

public interface RingDisruptorService {

    void addDisruptor(String disruptorName, Disruptor<OrderEvent> disruptor);

    void removeDisruptor(String disruptorName);

    void publishOrder(OrderEvent order);

    List<Disruptor<OrderEvent>> getDisruptors();
}
