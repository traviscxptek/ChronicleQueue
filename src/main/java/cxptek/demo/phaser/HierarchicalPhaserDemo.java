package cxptek.demo.phaser;

import java.util.concurrent.Phaser;

public class HierarchicalPhaserDemo {
    public static void start() {
        Phaser parentPhaser = new Phaser(1); // Parent Phaser

        for (int i = 0; i < 2; i++) {
            Phaser childPhaser = new Phaser(parentPhaser);

            for (int j = 1; j <= 2; j++) {
                int threadId = i * 10 + j;
                childPhaser.register();

                new Thread(() -> {
                    System.out.println("Thread " + threadId + " performing work.");
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    childPhaser.arriveAndDeregister();
                    System.out.println("Thread " + threadId + " finished.");
                }).start();
            }
        }

        parentPhaser.arriveAndAwaitAdvance();
        System.out.println("All child tasks completed. Parent phaser advancing.");
        parentPhaser.arriveAndDeregister();
    }
}
