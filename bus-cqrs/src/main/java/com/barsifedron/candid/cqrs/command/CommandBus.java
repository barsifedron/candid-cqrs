package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.command.middleware.Chain;
import com.barsifedron.candid.cqrs.command.middleware.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.event.EventBus;

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

    public <T> CommandResponse<T> dispatch(Command<T> command);

    /**
     * This is in charge of dispatching the command to the right Command Handler.
     * This will only allow for one handler per command type. As it should be.
     */
    class Dispatcher implements CommandBus {

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
    class WithExecutionTime implements CommandBus {

        private final CommandBus next;
        private final static Logger LOGGER = Logger.getLogger(WithExecutionTime.class.getName());

        public WithExecutionTime(CommandBus next) {
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
    class WithFiltering<V> implements CommandBus {

        private final Class<? extends V> filteringClass;
        private final CommandBus next;

        public WithFiltering(Class<? extends V> filteringClass, CommandBus next) {
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
        private final CommandBus next;

        public WithEventDispatch(EventBus eventBus, CommandBus next) {
            this.eventBus = eventBus;
            this.next = next;
        }

        public <T> T dispatch(Command<T> command) {
            CommandResponse<T> response = next.dispatch(command);
            response.events.forEach(evt -> eventBus.dispatch(evt));
            return response.result;
        }
    }


    /**
     * A Simple example factory. more advanced examples are provided in the other modules
     */
    class ExampleFactory {

        private final Set<CommandHandler> commandHandlers;

        /**
         * This should totally be injected by you dependency injection tool. See examples in other modules.
         */
        public ExampleFactory(Set<CommandHandler> commandHandlers) {
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