package cxptek.service;

import java.time.Instant;

public class SnowflakeIdGenerator {

    private final long epoch;
    private final long machineId;
    private final long machineIdBits = 10L;
    private final long sequenceBits = 12L;

    private final long maxMachineId = ~(-1L << machineIdBits);
    private final long sequenceMask = ~(-1L << sequenceBits);

    private final long machineIdShift = sequenceBits;
    private final long timestampShift = sequenceBits + machineIdBits;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(long machineId, long epoch) {
        if (machineId < 0 || machineId > maxMachineId) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + maxMachineId);
        }
        this.machineId = machineId;
        this.epoch = epoch;
    }

    public synchronized long generateId() {
        long currentTimestamp = getCurrentTimestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate ID.");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // Wait for the next millisecond
                currentTimestamp = waitNextMillisecond(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - epoch) << timestampShift)
                | (machineId << machineIdShift)
                | sequence;
    }

    private long waitNextMillisecond(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    private long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    public static void main(String[] args) {
        long customEpoch = 1672531200000L;
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, customEpoch);

        for (int i = 0; i < 10; i++) {
            System.out.println(generator.generateId());
        }
    }
}
