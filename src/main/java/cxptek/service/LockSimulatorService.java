package cxptek.service;

public interface LockSimulatorService {

    void syncLock();

    void syncLock(long orderId);

    void reentrantLock(long orderId);

    void reentrantUnlock(long orderId);

    long updateOrderId(long orderId);
}
