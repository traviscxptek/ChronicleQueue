package cxptek.service.iml;

import com.google.inject.Inject;
import cxptek.dto.event.OrderEvent;
import cxptek.service.HashService;
import cxptek.service.RingExecutorService;
import cxptek.utils.HashingUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static cxptek.utils.Constants.VIRTUAL_RING_SIZE;

@Slf4j
public class RingExecutorServiceImpl implements RingExecutorService {

    private final HashService hashService;

    private final TreeMap<Long, String> ringExecutorMap = new TreeMap<>();
    private final Map<String, ExecutorService> executorMap = new ConcurrentHashMap<>();

    @Inject
    public RingExecutorServiceImpl(HashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public void addRingExecutor(String executorName) {
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        for (int i = 0; i < VIRTUAL_RING_SIZE; i++) {
            long hash = hashKey(executorName + "_" + i);
            ringExecutorMap.put(hash, executorName);
        }
        executorMap.put(executorName, executorService);
    }

    @Override
    public void clearRingExecutors() {
        executorMap.clear();
        ringExecutorMap.clear();
    }

    @Override
    public void removeRingExecutor(String executorName) {
        if (!executorMap.containsKey(executorName)) {
            return;
        }
        ExecutorService remove = executorMap.remove(executorName);
        remove.close();
        for (int i = 0; i < VIRTUAL_RING_SIZE; i++) {
            long hash = hashKey(executorName + "_" + i);
            ringExecutorMap.remove(hash);
        }
    }

    @Override
    public Future<OrderEvent> submitTask(OrderEvent order, Consumer<OrderEvent> consumer) {
        long hash = hashKey(HashingUtils.buildOrderHashKey(order));
        Map.Entry<Long, String> entry = ringExecutorMap.ceilingEntry(hash);
        while (entry == null && hash > VIRTUAL_RING_SIZE) {
            hash = hash / executorMap.size();
            entry = ringExecutorMap.ceilingEntry(hash);
        }
        String executorName = ringExecutorMap.ceilingEntry(hash).getValue();
        if (!executorMap.containsKey(executorName)) {
            log.info("Ring executor hash {} not found executor service", hash);
            return null;
        }
        return executorMap.get(executorName)
                .submit(() -> {
                    consumer.accept(order);
                    return order;
                });
    }

    private long hashKey(String key) {
        return hashService.createXXHash(key);
    }
}
