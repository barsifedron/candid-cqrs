package com.barsifedron.candid.cqrs.domainevent;

import java.util.Collection;

/**
 * A event bus, aka a dispatcher.
 * Pretty similar to all the other ones
 */
public interface DomainEventBus {

    void dispatch(DomainEvent event);

    /**
     * Main event bus
     */
    class Default implements DomainEventBus {

        private final Collection<? extends DomainEventHandler> handlers;

        public Default(Collection<? extends DomainEventHandler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public void dispatch(DomainEvent event) {
            handlers
                    .stream()
                    .filter(handler -> handler.listenTo() == event.getClass())
                    .forEach(handler -> handler.handle(event));
        }
    }
}