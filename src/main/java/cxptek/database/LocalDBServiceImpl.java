package cxptek.database;

import cxptek.dto.event.OrderEvent;

import java.util.HashMap;
import java.util.Map;

public class LocalDBServiceImpl implements LocalDBService {

    private final Map<Long, OrderEvent> shardDB1;
    private final Map<Long, OrderEvent> shardDB2;
    private final Map<Long, OrderEvent> shardDB3;

    public LocalDBServiceImpl() {
        shardDB1 = new HashMap<>();
        shardDB2 = new HashMap<>();
        shardDB3 = new HashMap<>();
    }

    @Override
    public boolean upsertOrder(OrderEvent order) {
        shardDB1.put(order.getId(), order);
        return false;
    }
}
