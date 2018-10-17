package com.barsifedron.candid.cqrs.domainevent;

/**
 * A event bus, aka a dispatcher.
 * Pretty similar to all the other ones
 */
public interface DomainEventBus {

    void dispatch(DomainEvent event);

}