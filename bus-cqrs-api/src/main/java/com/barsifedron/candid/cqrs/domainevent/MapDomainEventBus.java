package com.barsifedron.candid.cqrs.domainevent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * This is in charge of dispatching the domain events to the right Handler.
 * This will only allow fowr many handlers per event. As it should be.
 */
public class MapDomainEventBus implements DomainEventBus {

    private final Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> handlers;

    public MapDomainEventBus(DomainEventHandler... handlers) {
        this(Stream.of(handlers).collect(Collectors.toSet()));
    }

    public MapDomainEventBus(Set<? extends DomainEventHandler> set) {
        this(set.stream().collect(toMap(
                handler -> handler.listenTo(),
                handler -> Stream.of((Supplier<DomainEventHandler>) () -> handler).collect(toList()),
                (list1, list2) -> Stream.of(list1, list2).flatMap(Collection::stream).collect(toList())
        )));
    }

    /**
     * Your set of handlers should be given to you by your dependency injection tool.
     * See examples in others modules.
     */
    public MapDomainEventBus(Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void dispatch(DomainEvent event) {
        handlers
                .getOrDefault(event.getClass(), Collections.emptyList())
                .forEach(handler -> handler.get().handle(event));
    }

}

