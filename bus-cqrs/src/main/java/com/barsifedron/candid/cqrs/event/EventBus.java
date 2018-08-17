package com.barsifedron.candid.cqrs.event;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * A event bus, aka a dispatcher.
 */
public interface EventBus {

    void dispatch(Event event);

    /**
     * Main event bus
     */
    class Default implements EventBus {

        private final Collection<? extends EventHandler> handlers;

        public Default(Collection<? extends EventHandler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public void dispatch(Event event) {
            handlers
                    .stream()
                    .filter(handler -> ((EventHandler) handler).listenTo() == event.getClass())
                    .forEach(handler -> ((EventHandler) handler).handle(event));
        }
    }

    /**
     * A decorator doing some Logging for us
     */
    class LoggingMiddleware implements EventBus {

        private final EventBus next;
        private static final Logger LOGGER = Logger.getLogger(LoggingMiddleware.class.getName());

        public LoggingMiddleware(EventBus next) {
            this.next = next;
        }

        @Override
        public void dispatch(Event event) {

            LOGGER.info("Processing event of type : " + event.getClass().getName());

            long timeBefore = System.nanoTime();
            next.dispatch(event);
            long timeAfter = System.nanoTime();

            LOGGER.info("" +
                    "Done Processing event of type " + event.getClass().getName() +
                    "\nExecution time was " + (timeAfter - timeBefore) / 1000000 + " ms "
            );
        }

    }
}