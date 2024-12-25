package org.dto.event;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;

public class BlockingFreeWaitStrategy implements WaitStrategy {

    @Override
    public long waitFor(long sequence, Sequence cursor, Sequence dependentSequence, SequenceBarrier barrier)
            throws AlertException, InterruptedException, TimeoutException {
        return sequence;
    }

    @Override
    public void signalAllWhenBlocking() {
        //TODO blocking free so do nothing
    }
}
