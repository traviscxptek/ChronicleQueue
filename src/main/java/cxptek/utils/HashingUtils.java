package cxptek.utils;

import cxptek.dto.event.OrderEvent;

public class HashingUtils {

    public static String buildOrderHashKey(OrderEvent order) {
        return order.getId() + "_" + order.getUserId() + "_" + order.getCreatedAt().getTime();
    }
}
