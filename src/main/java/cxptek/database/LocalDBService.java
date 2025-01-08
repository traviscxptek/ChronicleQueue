package cxptek.database;

import cxptek.dto.event.OrderEvent;

public interface LocalDBService {


    boolean upsertOrder(OrderEvent order);
}
