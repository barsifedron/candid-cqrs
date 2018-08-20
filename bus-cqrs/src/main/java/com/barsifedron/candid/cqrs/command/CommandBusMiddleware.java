package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.event.EventBus;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toMap;

/**
 * See an alternative way of doing this in the `bus-cqs` module.
 */
public interface CommandBusMiddleware {

    public <T> CommandResponse<T> dispatch(Command<T> command);

    /**
     * This is in charge of dispatching the command to the right Command Handler.
     * This will only allow for one handler per command type. As it should be.
     */
    class Dispatcher implements CommandBusMiddleware {

        private final Map<Class, CommandHandler> handlers;
        private final static Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());

        /**
         * The set of handlers will usually be injected by your dependency injection tool.
         * Examples for this can be found in the other modules.
         */
        public Dispatcher(Set<? extends CommandHandler> commandHandlers) {
            this(commandHandlers.stream().collect(
                    toMap(
                            handler -> handler.listenTo(),
                            handler -> handler)));
        }

        public Dispatcher(Map<Class, CommandHandler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public <T> CommandResponse<T> dispatch(Command<T> command) {
            CommandHandler<T, Command<T>> handler = Optional
                    .ofNullable(command)
                    .map(c -> c.getClass())
                    .map(handlers::get)
                    .orElseThrow(() -> {
                        LOGGER.info("Could not find handler for command  of type :" + command.getClass().getName());
                        return new CommandHandlerNotFoundException(command.getClass());
                    });
            return handler.handle(command);
        }

        private class CommandHandlerNotFoundException extends RuntimeException {
            public CommandHandlerNotFoundException(Class<? extends Command> aClass) {
                super("Could not find Command Handler for command of type " + aClass.getName());
            }
        }
    }

    /**
     * For the sake of providing an example of a decorating function:
     * A decorator calculating the execution time of our commands handling.
     * <p>
     * Now it is up to you to create others! Some possible things here : Validating your command dtos,
     * wrapping the command execution within a database transaction, etc...
     * The sky is your limit
     */
    class WithExecutionTime implements CommandBusMiddleware {

        private final CommandBusMiddleware next;
        private final static Logger LOGGER = Logger.getLogger(WithExecutionTime.class.getName());

        public WithExecutionTime(CommandBusMiddleware next) {
            this.next = next;
        }

        public <T> CommandResponse<T> dispatch(Command<T> command) {
            LOGGER.info("Processing simple command of type :" + command.getClass().getName());

            long timeBefore = System.nanoTime();
            CommandResponse<T> result = next.dispatch(command);
            long timeAfter = System.nanoTime();
            LOGGER.info("" +
                    "Done processing simple command of type" + command.getClass().getName() +
                    "\nExecution time was :" + ((timeAfter - timeBefore) / 1000000) + " ms");
            return result;
        }
    }

    /**
     * For the sake of providing an example of a decorating function:
     * A decorator filtering commands, and only processing them if they implement a certain interface.
     */
    class WithFiltering<V> implements CommandBusMiddleware {

        private final Class<? extends V> filteringClass;
        private final CommandBusMiddleware next;

        public WithFiltering(Class<? extends V> filteringClass, CommandBusMiddleware next) {
            this.filteringClass = filteringClass;
            this.next = next;
        }

        public <T> CommandResponse<T> dispatch(Command<T> command) {
            if (command.getClass().isInstance(filteringClass)) {
                return next.dispatch(command);
            }
            return null;
        }
    }


    class WithEventDispatch {

        private final EventBus eventBus;
        private final CommandBusMiddleware next;

        public WithEventDispatch(EventBus eventBus, CommandBusMiddleware next) {
            this.eventBus = eventBus;
            this.next = next;
        }

        public <T> T dispatch(Command<T> command) {
            CommandResponse<T> response = next.dispatch(command);
            response.events.forEach(evt -> eventBus.dispatch(evt));
            return response.result;
        }
    }

}
