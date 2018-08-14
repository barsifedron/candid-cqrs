package com.barsifedron.candid.cqs.command;

import com.barsifedron.candid.cqs.command.middleware.Chain;
import com.barsifedron.candid.cqs.command.middleware.CommandMiddleware;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * A really simple command bus. Dispatches a command to its handler.
 * <p>
 * Inspired by the first part of this great presentation:
 * https://speakerdeck.com/lilobase/cqrs-fonctionnel-event-sourcing-and-domain-driven-design-breizhcamp-2017
 * <p>
 * See also : `https://www.slideshare.net/rosstuck/command-bus-to-awesome-town`
 */
public interface CommandBus {

    <T> T dispatch(SimpleCommand<T> command);

    /**
     * This is in charge of dispatching the command to the right Command Handler.
     * This will only allow for one handler per command type. As it should be.
     */
    class Dispatcher implements CommandBus {

        private final Map<Class, SimpleCommandHandler> handlers;
        private final static Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());

        /**
         * The set of handlers will usually be injected by your dependency injection tool.
         * Examples for this can be found in the other modules.
         */
        public Dispatcher(Set<? extends SimpleCommandHandler> commandHandlers) {
            this(commandHandlers.stream().collect(
                    toMap(
                            handler -> handler.listenTo(),
                            handler -> handler)));
        }

        public Dispatcher(Map<Class, SimpleCommandHandler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public <T> T dispatch(SimpleCommand<T> command) {
            SimpleCommandHandler<T, SimpleCommand<T>> handler = Optional
                    .ofNullable(command)
                    .map(c -> c.getClass())
                    .map(handlers::get)
                    .orElseThrow(() -> {
                        LOGGER.info("Could not find handler for command  of type :" + command.getClass().getName());
                        return new SimpleCommandHandlerNotFoundException(command.getClass());
                    });
            return handler.handle(command);
        }

        private class SimpleCommandHandlerNotFoundException extends RuntimeException {
            public SimpleCommandHandlerNotFoundException(Class<? extends SimpleCommand> aClass) {
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
    class WithExecutionTime implements CommandBus {

        private final CommandBus next;
        private final static Logger LOGGER = Logger.getLogger(WithExecutionTime.class.getName());

        public WithExecutionTime(CommandBus next) {
            this.next = next;
        }

        public <T> T dispatch(SimpleCommand<T> command) {
            LOGGER.info("Processing simple command of type :" + command.getClass().getName());

            long timeBefore = System.nanoTime();
            T result = next.dispatch(command);
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
    class WithFiltering<V> implements CommandBus {

        private final Class<? extends V> filteringClass;
        private final CommandBus next;

        public WithFiltering(Class<? extends V> filteringClass, CommandBus next) {
            this.filteringClass = filteringClass;
            this.next = next;
        }

        public <T> T dispatch(SimpleCommand<T> command) {
            if (command.getClass().isInstance(filteringClass)) {
                return next.dispatch(command);
            }
            return null;
        }
    }


    /**
     * A Simple example factory. more advanced examples are provided in the other modules
     */
    class ExampleFactory {

        private final Set<SimpleCommandHandler> commandHandlers;

        /**
         * This should totally be injected by you dependency injection tool. See examples in other modules.
         */
        public ExampleFactory(Set<SimpleCommandHandler> commandHandlers) {
            this.commandHandlers = commandHandlers;
        }

        /**
         * Keep wrapping them with other decorators to add behavior. If it becomes too much, why not look at the middleware way of doing things
         * in the middleware package?
         */
        public CommandBus simpleCommandBus() {
            return new WithExecutionTime(new Dispatcher(commandHandlers));
        }

        /**
         * A bus filtering (and only accepting to process) commands that also implement the Serializable interface.
         * Not sure why one would want to do that but it makes for an example of wrapping command buses to obtain complex behaviors.
         */
        public CommandBus simpleFilteringCommandBus() {
            return new WithFiltering(
                    Serializable.class,
                    new WithExecutionTime(
                            new Dispatcher(commandHandlers)));
        }


    }
}