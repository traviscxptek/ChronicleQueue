package cxptek.utils;

public class LockUtils {

    public static void lockCurrentThread(long lockingTime) {
        try {
            Thread.sleep(lockingTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
