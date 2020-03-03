package com.barsifedron.candid.cqrs.domainevent;

import com.barsifedron.candid.cqrs.domainevent.middleware.DomainEventBusDispatcher;

import java.util.Set;

public class EventBusFactory {

    private final Set<DomainEventHandler> handlers;

    /**
     * Should come from your Dependency Injection tool
     */
    public EventBusFactory(Set<DomainEventHandler> handlers) {
        this.handlers = handlers;
    }

    public DomainEventBus build() {
        return DomainEventBusMiddleware.chainManyIntoADomainEventBus(
                new DomainEventBusDispatcher(handlers)
        );
    }
}
