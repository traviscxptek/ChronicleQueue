package cxptek.dto.event;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.io.InvalidMarshallableException;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

@Slf4j
@SuperBuilder
public class OrderEvent extends BaseOrderEvent {

    @Override
    public String getPayload() {
        return " Order Id:  %d  status: %s ".formatted(this.id, this.status);
    }

    @Override
    public String toString() {
        return this.getPayload();
    }

    @Override
    public void readMarshallable(BytesIn<?> bytes)
            throws IORuntimeException,
            BufferUnderflowException,
            IllegalStateException,
            InvalidMarshallableException {
        log.info("OrderEvent readMarshallable");
        super.readMarshallable(bytes);
    }

    @Override
    public void writeMarshallable(BytesOut<?> bytes)
            throws IllegalStateException,
            BufferOverflowException,
            BufferUnderflowException,
            ArithmeticException,
            InvalidMarshallableException {
        log.info("OrderEvent writeMarshallable");
        super.writeMarshallable(bytes);
    }
}
