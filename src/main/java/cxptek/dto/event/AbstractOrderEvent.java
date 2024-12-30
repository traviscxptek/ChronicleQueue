package cxptek.dto.event;

import cxptek.dto.event.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.openhft.chronicle.bytes.BytesMarshallable;

import java.io.Serializable;

@Setter
@Getter
@SuperBuilder
public abstract class AbstractOrderEvent implements BytesMarshallable, Serializable {
    protected Long id; //maybe NULL
    protected String message;
    protected OrderStatus status;

    public abstract String getPayload();
}
