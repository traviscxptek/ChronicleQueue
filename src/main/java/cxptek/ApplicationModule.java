package cxptek;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import cxptek.demo.ChronicleQueueDemo;
import cxptek.demo.DistributedEventDemo;
import cxptek.demo.WorkerPoolEventDemo;
import cxptek.service.DisruptorService;
import cxptek.service.HashService;
import cxptek.service.LocalQueueService;
import cxptek.service.RingDisruptorService;
import cxptek.service.RingExecutorService;
import cxptek.service.RingQueueService;
import cxptek.service.SimulatorLockService;
import cxptek.service.iml.DisruptorServiceImpl;
import cxptek.service.iml.LocalQueueServiceImpl;
import cxptek.service.iml.HashServiceImpl;
import cxptek.service.iml.RingDisruptorServiceImpl;
import cxptek.service.iml.RingExecutorServiceImpl;
import cxptek.service.iml.RingQueueServiceImpl;
import cxptek.service.iml.SimulatorLockServiceImpl;


public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        //start application init
        bind(SimulatorLockService.class).to(SimulatorLockServiceImpl.class).in(Singleton.class);
        bind(DisruptorService.class).toInstance(new DisruptorServiceImpl());
        bind(HashService.class).toInstance(new HashServiceImpl());
        bind(LocalQueueService.class).toInstance(new LocalQueueServiceImpl());
        bind(RingQueueService.class).to(RingQueueServiceImpl.class).in(Singleton.class);
        bind(RingExecutorService.class).to(RingExecutorServiceImpl.class).in(Singleton.class);
        bind(RingDisruptorService.class).to(RingDisruptorServiceImpl.class).in(Singleton.class);

        //init demo service
        bind(ChronicleQueueDemo.class).toInstance(new ChronicleQueueDemo());
        bind(DistributedEventDemo.class).in(Singleton.class);
        bind(WorkerPoolEventDemo.class).in(Singleton.class);

    }
}