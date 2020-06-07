package com.barsifedron.candid.cqrs.springboot.app.library.infrastructure;

import com.barsifedron.candid.cqrs.springboot.app.library.domain.ThingAggegate;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.ThingId;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.ThingRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryThingRepository implements ThingRepository {

    private final Map<ThingId, ThingAggegate> map;

    public InMemoryThingRepository() {
        this(new HashMap<>());
    }


    public InMemoryThingRepository(Map<ThingId, ThingAggegate> map) {
        this.map = map;
    }

    @Override
    public void add(ThingAggegate item) {
        map.put(item.id(), item);
    }

}
