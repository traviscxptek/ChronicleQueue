package cxptek.disruption.processor;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.Sequencer;
import com.lmax.disruptor.TimeoutException;
import cxptek.dto.event.BaseOrderEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public final class CxptekEventProcessor<T extends BaseOrderEvent> implements EventProcessor {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Sequence sequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
    private final RingBuffer<T> ringBuffer;
    private final SequenceBarrier sequenceBarrier;
    private final EventHandler<T> eventHandler;
    private final ExceptionHandler<T> exceptionHandler;
    private final Sequence workSequence;

    /**
     * Construct a {@link CxptekEventProcessor}.
     *
     * @param ringBuffer       to which events are published.
     * @param sequenceBarrier  on which it is waiting.
     * @param eventHandler     is the delegate to which events are dispatched.
     * @param exceptionHandler to be called back when an error occurs
     * @param workSequence     from which to claim the next event to be worked on.  It should always be initialised
     *                         as {@link Sequencer#INITIAL_CURSOR_VALUE}
     */
    public CxptekEventProcessor(
            RingBuffer<T> ringBuffer,
            SequenceBarrier sequenceBarrier,
            EventHandler<T> eventHandler,
            ExceptionHandler<T> exceptionHandler,
            Sequence workSequence) {
        this.ringBuffer = ringBuffer;
        this.sequenceBarrier = sequenceBarrier;
        this.eventHandler = eventHandler;
        this.exceptionHandler = exceptionHandler;
        this.workSequence = workSequence;
    }

    @Override
    public Sequence getSequence() {
        return sequence;
    }

    @Override
    public void halt() {
        running.set(false);
        sequenceBarrier.alert();
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    /**
     * It is ok to have another thread re-run this method after a halt().
     *
     * @throws IllegalStateException if this processor is already running
     */
    @Override
    public void run() {
        checkBeforeStarting();
        sequenceBarrier.clearAlert();

        boolean processedSequence = true;
        long cachedAvailableSequence = Long.MIN_VALUE;
        long nextSequence = sequence.get();
        T event = null;
        while (true) {
            try {
                // if previous sequence was processed - fetch the next sequence and set
                // that we have successfully processed the previous sequence
                // typically, this will be true
                // this prevents the sequence getting too far forward if an exception
                // is thrown from the WorkHandler
                if (processedSequence) {
                    processedSequence = false;
                    do {
                        nextSequence = workSequence.get() + 1L;
                        sequence.set(nextSequence - 1L);
                    }
                    while (!workSequence.compareAndSet(nextSequence - 1L, nextSequence));
                }

                if (cachedAvailableSequence >= nextSequence) {
                    event = ringBuffer.get(nextSequence);
                    eventHandler.onEvent(event, nextSequence, false);
                    processedSequence = true;
                } else {
                    cachedAvailableSequence = sequenceBarrier.waitFor(nextSequence);
                }
            } catch (TimeoutException ex) {
                notifyTimeout(ex, sequence.get());
            } catch (AlertException ex) {
                if (!running.get()) {
                    break;
                }
            } catch (Throwable ex) {
                // handle, mark as processed, unless the exception handler threw an exception
                exceptionHandler.handleEventException(ex, nextSequence, event);
                processedSequence = true;
            }
        }
        running.set(false);
    }

    private void notifyTimeout(Exception ex, long availableSequence) {
        exceptionHandler.handleEventException(ex, availableSequence, null);
    }

    private void checkBeforeStarting() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Thread is already running");
        }
    }
}