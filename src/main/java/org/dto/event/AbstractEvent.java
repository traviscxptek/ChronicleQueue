package org.dto.event;

import lombok.Getter;
import lombok.Setter;
import net.openhft.chronicle.bytes.BytesMarshallable;

import java.io.Serializable;

@Setter
@Getter
public abstract class AbstractEvent implements BytesMarshallable, Serializable {
    public long id;
    public String message;

    public abstract String getPayload();
}
