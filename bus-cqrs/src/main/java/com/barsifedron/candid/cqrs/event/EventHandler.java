package com.barsifedron.candid.cqrs.event;

public interface EventHandler<K extends Event> {

    void handle(K event);

    Class<K> listenTo();

}