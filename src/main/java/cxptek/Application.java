package cxptek;

import com.google.inject.Guice;
import com.google.inject.Injector;
import cxptek.demo.DistributedEventDemo;
import cxptek.demo.MultipleDistributedEventDemo;

public class Application {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApplicationModule());
//        WorkerPoolEventDemo workerPoolEventDemo = injector.getInstance(WorkerPoolEventDemo.class);
//        workerPoolEventDemo.start();

//        DistributedEventDemo distributedEventDemo = injector.getInstance(DistributedEventDemo.class);
//        distributedEventDemo.start();

        MultipleDistributedEventDemo multipleDistributedEventDemo = injector.getInstance(MultipleDistributedEventDemo.class);
        multipleDistributedEventDemo.start();
    }
}
