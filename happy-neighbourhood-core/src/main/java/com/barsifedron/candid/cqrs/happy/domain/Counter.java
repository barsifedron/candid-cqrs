package com.barsifedron.candid.cqrs.happy.domain;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class Counter {

    ItemId itemId;
    Integer count;
    LocalDate localDate;

    public void increment() {
        count = count + 1;
    }
}
