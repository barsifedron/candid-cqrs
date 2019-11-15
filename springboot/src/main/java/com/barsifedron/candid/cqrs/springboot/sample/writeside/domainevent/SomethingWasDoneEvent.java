package com.barsifedron.candid.cqrs.springboot.sample.writeside.domainevent;

import com.barsifedron.candid.cqrs.domain.DomainEventToLog;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

public class SomethingWasDoneEvent implements DomainEvent, DomainEventToLog {

    public final String someoneToNotify;

    public final Integer somethingElse;

    public SomethingWasDoneEvent(String someoneToNotify, Integer someOtherInfoAboutTheThingDone) {
        this.someoneToNotify = someoneToNotify;
        this.somethingElse = someOtherInfoAboutTheThingDone;
    }

    /**
     * The logging intention expressed with the DomainEventToLog interface will result in a call the toString method.
     */
    @Override
    public String toString() {
        return "SomethingWasDoneEvent{" +
                "someoneToNotify='" + someoneToNotify + '\'' +
                ", somethingElse=" + somethingElse +
                '}';
    }
}
