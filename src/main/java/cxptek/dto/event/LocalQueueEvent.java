package cxptek.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CountDownLatch;

@Getter
@Setter
@Builder
public class LocalQueueEvent {

    private OrderEvent order;
    private CountDownLatch countDownLatch;
}
