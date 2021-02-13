package com.barsifedron.candid.cqrs.happy.domain;

import java.time.LocalDate;

/**
 * The implementation of this can hit a different database.
 * Or communicate with an external service through REST or any other way you need/want.
 * It is up to you.
 */
public interface ItemsCounterRepository {

    Counter dailyCounter(LocalDate day, ItemId itemId);

    void save(Counter counter);
}
