//package cxptek.main;
//
//import com.lmax.disruptor.BatchEventProcessor;
//import com.lmax.disruptor.IgnoreExceptionHandler;
//import com.lmax.disruptor.RingBuffer;
//import com.lmax.disruptor.WorkerPool;
//import cxptek.dto.event.LongEvent;
//import cxptek.dto.event.LongEventFactory;
//import cxptek.dto.event.LongWorkerHandler;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//
//public class WorkerPoolEvents {
//
//    public static void main(String[] args) throws Exception {
//        long startTime = System.currentTimeMillis();
//        Executor executor = new ScheduledThreadPoolExecutor(5);
//        WorkerPool<LongEvent> workerPool = new WorkerPool<>(new LongEventFactory(),
//                new IgnoreExceptionHandler(),
//                new LongWorkerHandler("Worker 1"),
//                new LongWorkerHandler("Worker 2"),
//                new LongWorkerHandler("Worker 3"),
//                new LongWorkerHandler("Worker 4"),
//                new LongWorkerHandler("Worker 5"));
//
//        RingBuffer<LongEvent> ringBuffer = workerPool.start(executor);
//        for (long index = 0; index < 100; index++) {
//            long nextSequence = ringBuffer.next();
//            LongEvent longEvent = ringBuffer.getPreallocated(nextSequence);
//            longEvent.setMessage("Event " + index);
//            longEvent.setId(index);
//            ringBuffer.publish(nextSequence);
//        }
//
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            System.out.println("events consumer time: " + (System.currentTimeMillis() - startTime));
//        }));
//    }
//}
