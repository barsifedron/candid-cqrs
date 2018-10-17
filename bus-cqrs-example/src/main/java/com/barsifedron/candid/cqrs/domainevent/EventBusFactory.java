package com.barsifedron.candid.cqrs.domainevent;

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
        DomainEventBusMiddlewareChain chain = new DomainEventBusMiddlewareChain.Factory().chainOfMiddleware(
                new DomainEventBusMiddleware.Dispatcher(handlers)
        );
        return chain::dispatch;
    }
}
