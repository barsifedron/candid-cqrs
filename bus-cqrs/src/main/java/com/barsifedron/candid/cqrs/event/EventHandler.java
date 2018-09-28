package com.barsifedron.candid.cqrs.event;

/**
 * Contrarily to the commands and queries, where one single handler is the rule, events
 * can have as many listeners/handlers as you want.
 */
public interface EventHandler<K extends Event> {

    void handle(K event);

    Class<K> listenTo();

}