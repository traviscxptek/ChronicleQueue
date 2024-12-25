package org.dto.event;

import com.lmax.disruptor.EventHandler;

public class LongEventHandler implements EventHandler<LongEvent> {
    private String name;

    public LongEventHandler() {
    }

    public LongEventHandler(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
        event.setMessage(name);
        System.out.println(name + " Event: " + event + ", sequence: " + sequence
                + ", endOfBatch: " + endOfBatch + ", message: " + event.getMessage());
    }
}
