package com.barsifedron.candid.cqrs.springboot.sample.writeside.domainevent;

import com.barsifedron.candid.cqrs.domain.DomainEventToLog;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

public class SomethingWasDoneEvent implements DomainEvent, DomainEventToLog {

    public final String someoneToNotify;

    public final Integer somethingElse;

    /**
     * Not a big fan of lombok but there really is no shame in using it for this
     */
    public SomethingWasDoneEvent(String someoneToNotify, Integer someOtherInfoAboutTheThingDone) {
        this.someoneToNotify = someoneToNotify;
        this.somethingElse = someOtherInfoAboutTheThingDone;
    }

    /**
     * The logging intention expressed with the DomainEventToLog interface will result in a call the toString method.
     * <p>
     * Not a big fan of lombok but there really is no shame in using it for this
     */
    @Override
    public String toString() {
        return "SomethingWasDoneEvent{" +
                "someoneToNotify='" + someoneToNotify + '\'' +
                ", somethingElse=" + somethingElse +
                '}';
    }
}
