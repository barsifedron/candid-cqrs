package com.barsifedron.candid.cqrs.event;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * A event bus, aka a dispatcher.
 * Pretty similar to all the other ones
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
    class WithExecutionDurationLogging implements EventBus {

        private final EventBus next;
        private static final Logger LOGGER = Logger.getLogger(WithExecutionDurationLogging.class.getName());

        public WithExecutionDurationLogging(EventBus next) {
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