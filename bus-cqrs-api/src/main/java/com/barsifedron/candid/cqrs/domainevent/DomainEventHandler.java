package com.barsifedron.candid.cqrs.domainevent;

/**
 * Contrarily to the commands and queries, where one single handler is the rule, events
 * can have as many listeners/handlers as you want.
 */
public interface DomainEventHandler<K extends DomainEvent> {

    void handle(K event);

    Class<K> listenTo();

}