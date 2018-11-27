package com.barsifedron.candid.cqrs.domainevent.middleware;

import com.barsifedron.candid.cqrs.domainevent.*;

import java.util.Set;

/**
 * This is in charge of dispatching the domain events to the right Handler.
 * This will only allow for many handlers per event. As it should be.
 */
public class DomainEventBusDispatcher implements DomainEventBusMiddleware {

    private final Set<? extends DomainEventHandler> handlers;

    /**
     * Your set of handlers should be given to you by your dependency injection tool.
     * See examples in others modules.
     */
    public DomainEventBusDispatcher(Set<? extends DomainEventHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void dispatch(DomainEvent event, DomainEventBus notUsed) {
        handlers
                .stream()
                .filter(handler -> handler.listenTo() == event.getClass())
                .forEach(handler -> handler.handle(event));
    }

}