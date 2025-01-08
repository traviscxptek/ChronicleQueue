package cxptek.service.iml;

import cxptek.service.LockSimulatorService;
import cxptek.utils.LockUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockSimulatorServiceImpl implements LockSimulatorService {

    private static final List<Long> LOCK_TIMES = List.of(50L, 100L, 150L, 200L);
    private static final Random RANDOM = new Random();

    private final Lock reentrantLock = new ReentrantLock();

    private volatile long orderId = -1L;

    private final Object mutex = new Object();

    @Override
    public void syncLock() {
        synchronized (mutex) {
            LockUtils.lockCurrentThread(getRandomLockTime());
        }
    }

    @Override
    public void syncLock(long orderId) {
        synchronized (mutex) {
            System.out.printf("Thread: %d syncLock orderId %d\n", Thread.currentThread().threadId(), orderId);
        }
    }

    @Override
    public void reentrantLock(long orderId) {
        System.out.printf("Thread: %d reentrantLock orderId %s\n", Thread.currentThread().threadId(), orderId);
        reentrantLock.lock();
    }

    @Override
    public void reentrantUnlock(long orderId) {
        System.out.printf("Thread: %d reentrantUnlock orderId %d\n", Thread.currentThread().threadId(), orderId);
        reentrantLock.unlock();
    }

    @Override
    public long updateOrderId(long orderId) {
        LockUtils.lockCurrentThread(getRandomLockTime());
        this.orderId = orderId;
        System.out.printf("Thread: %d updateOrderId: %d\n", Thread.currentThread().threadId(), orderId);
        return this.orderId;
    }

    private Long getRandomLockTime() {
        return LOCK_TIMES.get(RANDOM.nextInt(LOCK_TIMES.size()));
    }
}
