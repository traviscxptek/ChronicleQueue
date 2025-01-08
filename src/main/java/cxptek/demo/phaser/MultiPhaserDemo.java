package cxptek.demo.phaser;

import java.util.concurrent.Phaser;

public class MultiPhaserDemo {
    public void start() {
        Phaser phaser = new Phaser(1);
        
        for (int i = 1; i <= 3; i++) {
            int threadId = i;
            phaser.register();
            new Thread(() -> {
                for (int phase = 0; phase < 3; phase++) {
                    System.out.println("Thread " + threadId + " performing work in phase " + phase);
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    phaser.arriveAndAwaitAdvance();
                }
                phaser.arriveAndDeregister();
                System.out.println("Thread " + threadId + " completed all phases.");
            }).start();
        }

        // Main thread participates in synchronization
        for (int phase = 0; phase < 3; phase++) {
            phaser.arriveAndAwaitAdvance(); // Wait for all threads to complete the phase
            System.out.println("Main thread advancing to next phase: " + phase);
        }

        phaser.arriveAndDeregister(); // Deregister main thread
        System.out.println("All phases completed. Main thread exiting.");
    }
}
