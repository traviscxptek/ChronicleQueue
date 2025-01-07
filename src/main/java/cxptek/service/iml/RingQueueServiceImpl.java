package cxptek.service.iml;

import com.google.inject.Inject;
import cxptek.dto.event.OrderEvent;
import cxptek.service.HashService;
import cxptek.service.LocalQueueService;
import cxptek.service.RingQueueService;
import cxptek.utils.HashingUtils;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import static cxptek.utils.Constants.VIRTUAL_RING_SIZE;

public class RingQueueServiceImpl implements RingQueueService {

    private final TreeMap<Integer, String> ringQueueMap;

    private final LocalQueueService localQueueService;
    private final HashService hashService;

    @Inject
    public RingQueueServiceImpl(LocalQueueService localQueueService,
                                HashService hashService) {
        this.localQueueService = localQueueService;
        this.hashService = hashService;
        this.ringQueueMap = new TreeMap<>();
    }

    @Override
    public void addQueue(String queueName) {
        localQueueService.addQueue(queueName);
        for (int i = 0; i < VIRTUAL_RING_SIZE; i++) {
            int hash = hashService.createMD5Hash(queueName + "_" + i);
            ringQueueMap.put(hash, queueName);
        }
    }

    @Override
    public void removeQueue(String queueName) {
        if (localQueueService.existsQueue(queueName)) {
            return;
        }
        localQueueService.removeQueue(queueName);
        for (int i = 0; i < VIRTUAL_RING_SIZE; i++) {
            int hash = hashService.createMD5Hash(queueName + "_" + i);
            ringQueueMap.remove(hash);
        }
    }

    @Override
    public void publishQueue(OrderEvent order) {
        int hash = hashService.createMD5Hash(HashingUtils.buildOrderHashKey(order));
        localQueueService.publishOrderEvent(order,
                new CountDownLatch(1),
                ringQueueMap.ceilingEntry(hash).getValue());
    }
}
