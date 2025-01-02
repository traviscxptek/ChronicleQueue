package cxptek.dto.event;

import cxptek.dto.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.openhft.chronicle.bytes.BytesMarshallable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Setter
@Getter
@SuperBuilder
public abstract class BaseOrderEvent implements BytesMarshallable, Serializable {
    protected Long id; //maybe NULL
    protected Long userId;
    protected String pairName; //DKG_USDT; USDT_BTC; BTC_BUSD ....
    protected String payload;
    protected OrderStatus status;
    protected BigDecimal totalAmount;
    protected Timestamp createdAt; //UTC timezone
}
