package cxptek.service.iml;

import com.google.inject.Inject;
import com.lmax.disruptor.dsl.Disruptor;
import cxptek.dto.event.OrderEvent;
import cxptek.service.HashService;
import cxptek.service.RingDisruptorService;
import cxptek.utils.HashingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static cxptek.utils.Constants.VIRTUAL_RING_SIZE;

public class RingDisruptorServiceImpl implements RingDisruptorService {

    private final TreeMap<Long, String> ringDisruptorMap;
    private final Map<String, Disruptor<OrderEvent>> disruptorMap;

    private final HashService hashService;

    @Inject
    public RingDisruptorServiceImpl(HashService hashService) {
        this.ringDisruptorMap = new TreeMap<>();
        this.disruptorMap = new HashMap<>();
        this.hashService = hashService;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            disruptorMap.values().forEach(Disruptor::shutdown);
        }));
    }

    @Override
    public void addDisruptor(String disruptorName, Disruptor<OrderEvent> disruptor) {
        for (int i = 0; i < VIRTUAL_RING_SIZE; i++) {
            ringDisruptorMap.put(hashService.createXXHash(disruptorName + "_" + i)
                    , disruptorName);
        }
        disruptorMap.put(disruptorName, disruptor);
    }

    @Override
    public void removeDisruptor(String disruptorName) {
        for (int i = 0; i < VIRTUAL_RING_SIZE; i++) {
            long hash = hashService.createXXHash(disruptorName + "_" + i);
            ringDisruptorMap.remove(hash);
        }
        disruptorMap.remove(disruptorName);
    }

    @Override
    public List<Disruptor<OrderEvent>> getDisruptors() {
        return new ArrayList<>(disruptorMap.values());
    }

    @Override
    public void publishOrder(OrderEvent order) {
        long hash = hashService.createXXHash(HashingUtils.buildOrderHashKey(order));
        Map.Entry<Long, String> entry = ringDisruptorMap.ceilingEntry(hash);
        while (entry == null) {
            hash = hash / disruptorMap.size();
            entry = ringDisruptorMap.ceilingEntry(hash);
        }
        String disruptorName = entry.getValue();
        if (!disruptorMap.containsKey(disruptorName)) {
            System.out.printf("Ring executor hash " + hash + " not found executor service \n");
        }
        Disruptor<OrderEvent> disruptor = disruptorMap.get(disruptorName);
        disruptor.getRingBuffer().publishEvent((orderEvent, sequence) -> {
            orderEvent.setId(order.getId());
            orderEvent.setStatus(order.getStatus());
            orderEvent.setCreatedAt(order.getCreatedAt());
            orderEvent.setPayload(order.getPayload());
            orderEvent.setPairName(order.getPairName());
            orderEvent.setUserId(order.getUserId());
            orderEvent.setTotalAmount(order.getTotalAmount());
        });
    }
}
