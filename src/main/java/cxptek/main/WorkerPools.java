//package cxptek.main;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutorService;
//
//import com.lmax.disruptor.*;
//import cxptek.dto.event.BatchEvent;
//import cxptek.dto.event.CxpTekWorkHandler;
//
///**
// * Demonstrates how to setup and use a WorkerPool.
// * <p>
// * Each processor in the WorkerPool runs in its own thread and the pool
// * grabs as many published events off the queue that it can and farms them
// * out to all the worker threads.  This means that you can have parallel
// * processing of published events - the parallel processing is dictated by
// * the size of the worker pool you specify/create.
// * <p>
// * A consequence of this is that the "batch" of published events just grabbed
// * are NOT PROCESSED in FIFO order, as this example output from this program
// * demonstrates:
// * [java] DemoWorkHandler #0 received event with ProcessID: 2
// * [java] DemoWorkHandler #0 received event with ProcessID: 3
// * [java] DemoWorkHandler #0 received event with ProcessID: 4
// * [java] DemoWorkHandler #0 received event with ProcessID: 5
// * [java] DemoWorkHandler #0 received event with ProcessID: 6
// * [java] DemoWorkHandler #0 received event with ProcessID: 7
// * [java] DemoWorkHandler #0 received event with ProcessID: 8
// * [java] DemoWorkHandler #0 received event with ProcessID: 9
// * [java] DemoWorkHandler #0 received event with ProcessID: 10
// * [java] DemoWorkHandler #0 received event with ProcessID: 11
// * [java] DemoWorkHandler #2 received event with ProcessID: 0
// * [java] DemoWorkHandler #2 received event with ProcessID: 13
// * [java] DemoWorkHandler #2 received event with ProcessID: 14
// * [java] DemoWorkHandler #1 received event with ProcessID: 1
// * [java] DemoWorkHandler #0 received event with ProcessID: 12
// * Note that the first published event (ProcessID = 0) was the 11th one
// * to be processed!
// * HOWEVER, if I put in a short sleep between each publication, they come
// * out in FIFO order:
// * [java] DemoWorkHandler #1 received event with ProcessID: 0
// * [java] DemoWorkHandler #0 received event with ProcessID: 1
// * [java] DemoWorkHandler #2 received event with ProcessID: 2
// * [java] DemoWorkHandler #1 received event with ProcessID: 3
// * [java] DemoWorkHandler #0 received event with ProcessID: 4
// * [java] DemoWorkHandler #2 received event with ProcessID: 5
// * [java] DemoWorkHandler #1 received event with ProcessID: 6
// * [java] DemoWorkHandler #0 received event with ProcessID: 7
// * [java] DemoWorkHandler #2 received event with ProcessID: 8
// * [java] DemoWorkHandler #1 received event with ProcessID: 9
// * [java] DemoWorkHandler #0 received event with ProcessID: 10
// * [java] DemoWorkHandler #2 received event with ProcessID: 11
// * [java] DemoWorkHandler #1 received event with ProcessID: 12
// * [java] DemoWorkHandler #0 received event with ProcessID: 13
// * [java] DemoWorkHandler #2 received event with ProcessID: 14
// */
//public class WorkerPools {
//
//    public static final int NUM_WORKERS = 3;
//    public static final int RING_BUFFER_SIZE = 256;
//    private final ExecutorService execService = Executors.newFixedThreadPool(NUM_WORKERS);
//    private final WorkerPool<BatchEvent> workerPool;
//
//    public WorkerPools() {
//        final CxpTekWorkHandler[] workHandlers = new CxpTekWorkHandler[NUM_WORKERS];
//        for (int i = 0; i < NUM_WORKERS; i++) {
//            workHandlers[i] = new CxpTekWorkHandler(i);
//        }
//
//        workerPool = new WorkerPool<>(BatchEvent.FACTORY,
//                new FatalExceptionHandler(),
//                workHandlers);
//    }
//
//    public void engage() {
//        // starts WorkerPool workers in separate thread(s)
//        RingBuffer<BatchEvent> ringBuf = workerPool.start(execService);
//
//        // publish lots of events
//        for (int i = 0; i < 5 * NUM_WORKERS; i++) {
//            long seq = ringBuf.next();
//            ringBuf.getPreallocated(seq).setProcessId(i);
//            ringBuf.publish(seq);
//            // try it with and without the sleep
//            // try { Thread.sleep(33); } catch (Exception e) { }
//        }
//
//        // wait until all published events are processed, then stop the workers
//        workerPool.drainAndHalt();
//    }
//
//    public static void main(String[] args) {
//        new WorkerPools().engage();
//    }
//}