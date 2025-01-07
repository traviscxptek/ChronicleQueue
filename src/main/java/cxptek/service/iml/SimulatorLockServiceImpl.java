package cxptek.service.iml;

import cxptek.service.SimulatorLockService;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimulatorLockServiceImpl implements SimulatorLockService {

    private final Lock lock = new ReentrantLock();

    @Override
    public void lock() {

    }
}
