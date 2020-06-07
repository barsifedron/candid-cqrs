package com.barsifedron.candid.cqrs.springboot.app.library.domain;

public interface ThingRepository {

    void add(ThingAggegate thingAggegate);

}
