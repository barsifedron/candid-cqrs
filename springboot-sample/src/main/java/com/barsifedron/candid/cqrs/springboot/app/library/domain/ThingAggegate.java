package com.barsifedron.candid.cqrs.springboot.app.library.domain;

public class ThingAggegate {

    private ThingId thingId;

    public ThingId id() {
        return thingId;
    }
}
