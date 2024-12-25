package org.dto.event;

import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.io.InvalidMarshallableException;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

@Slf4j
public class LongEvent extends AbstractEvent {

    public LongEvent() {
        super.id = System.currentTimeMillis();
    }

    @Override
    public String getPayload() {
        return String.valueOf(super.id);
    }

    @Override
    public String toString() {
        return this.getPayload();
    }

    @Override
    public void readMarshallable(BytesIn<?> bytes)
            throws IORuntimeException, BufferUnderflowException, IllegalStateException, InvalidMarshallableException {
        log.info("LongEvent readMarshallable");
        super.readMarshallable(bytes);
    }

    @Override
    public void writeMarshallable(BytesOut<?> bytes)
            throws IllegalStateException, BufferOverflowException, BufferUnderflowException, ArithmeticException, InvalidMarshallableException {
        log.info("LongEvent writeMarshallable");
        super.writeMarshallable(bytes);
    }
}
