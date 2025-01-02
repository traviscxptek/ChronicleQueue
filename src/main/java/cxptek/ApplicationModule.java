package cxptek;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import cxptek.demo.ChronicleQueueDemo;
import cxptek.demo.DistributedEventDemo;
import cxptek.demo.WorkerPoolEventDemo;
import cxptek.service.DisruptorService;
import cxptek.service.HashService;
import cxptek.service.LocalQueueService;
import cxptek.service.iml.DisruptorServiceImpl;
import cxptek.service.iml.LocalQueueServiceImpl;
import cxptek.service.iml.MD5HashServiceImpl;


public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        //start application init
        bind(DisruptorService.class).toInstance(new DisruptorServiceImpl());
        bind(HashService.class).toInstance(new MD5HashServiceImpl());
        bind(LocalQueueService.class).toInstance(new LocalQueueServiceImpl());

        //init demo service
        bind(ChronicleQueueDemo.class).toInstance(new ChronicleQueueDemo());
        bind(DistributedEventDemo.class).in(Singleton.class);
        bind(WorkerPoolEventDemo.class).in(Singleton.class);

    }
}