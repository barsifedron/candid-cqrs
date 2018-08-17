package com.barsifedron.candid.cqrs.command.middleware;

import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandHandler;


import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

/**
 * Proof of concept of the middleware pattern :
 * <p>
 * /!\ If you only have few extra behaviors, directly decorating the
 * CommandBus is a simpler and totally worth considering option. You
 * should start there. (as it is done in that class example factory)
 * <p>
 * Inspired by : https://www.slideshare.net/rosstuck/command-bus-to-awesome-town
 * <p>
 * As explained in the presentation above, the middleware pattern helps dealing
 * with addition of MANY decorators to the command bus behavior. Compared to
 * decorating the SimpleBus, this is not much different. We trade-off a bit of
 * added complexity for a bit more ease to wire when instantiating the bus.
 */
public interface CommandBusMiddleware {

    <T> CommandResponse<T> handle(Command<T> command, Function<Command<T>, CommandResponse<T>> next);

    /**
     * The middleware in charge of dispatching the command to the right Command Handler.
     * This will only allow for one handler per command type. As it should be.
     */
    class DispatcherMiddleware implements CommandBusMiddleware {

        private final Map<Class, CommandHandler> handlers;
        private final static Logger LOGGER = Logger.getLogger(DispatcherMiddleware.class.getName());


        /**
         * The set of handlers will usually be injected by your dependency injection tool.
         * Examples for this can be found in the other modules.
         */
        public DispatcherMiddleware(Set<CommandHandler> commandHandlers) {
            this(commandHandlers
                    .stream()
                    .collect(toMap(
                            handler -> handler.listenTo(),
                            handler -> handler)));
        }

        public DispatcherMiddleware(Map<Class, CommandHandler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public <T> CommandResponse<T> handle(Command<T> command, Function<Command<T>, CommandResponse<T>> next)

        {
            CommandHandler simpleCommandHandler = Optional
                    .ofNullable(command)
                    .map(c -> c.getClass())
                    .map(handlers::get)
                    .orElseThrow(() ->
                    {
                        LOGGER.info("Could not find handler for command  of type :" + command.getClass().getName());
                        return new CommandHandlerNotFoundException(command.getClass());
                    });
            return (CommandResponse<T>) simpleCommandHandler.handle(command);
        }

        private class CommandHandlerNotFoundException extends RuntimeException {
            public CommandHandlerNotFoundException(Class<? extends Command> aClass) {
                super("Could not find Command Handler for command of type " + aClass.getName());
            }
        }
    }


    /**
     * For the sake of providing an example of a decorating middleware:
     * A middleware calculating the execution time of our commands handling.
     * <p>
     * Now it is up to you to create others! Some possible things here : Validating your command dtos,
     * wrapping the command execution within a database transaction, etc...
     * The sky is your limit
     */
    class ExecutionTimeMiddleware implements CommandBusMiddleware {

        private final static Logger LOGGER = Logger.getLogger(ExecutionTimeMiddleware.class.getName());

        public ExecutionTimeMiddleware() {
        }

        @Override
        public <T> CommandResponse<T> handle(Command<T> command, Function<Command<T>, CommandResponse<T>> next)

        {

            LOGGER.info("Processing simple command of type :" + command.getClass().getName());

            long timeBefore = System.nanoTime();
            CommandResponse<T> result = next.apply(command);
            long timeAfter = System.nanoTime();

            LOGGER.info("Done processing simple command of type" + command.getClass().getName() + "Execution time was :" + ((timeAfter - timeBefore) / 1000000) + " ms");
            return result;
        }
    }

    /**
     * For the sake of providing an example of a decorating middleware:
     * A middleware filtering commands, and only processing them if they implement a certain interface.
     */
    class FilteringMiddleware<V> implements CommandBusMiddleware {

        private final Class<? extends V> filteringClass;

        public FilteringMiddleware(Class<? extends V> filteringClass) {
            this.filteringClass = filteringClass;
        }

        @Override
        public <T> CommandResponse<T> handle(Command<T> command, Function<Command<T>, CommandResponse<T>> next) {
            if (command.getClass().isInstance(filteringClass)) {
                return next.apply(command);
            }
            return null;
        }
    }


    /**
     * Example/Proof of concept.
     * /!\ : Always put dispatcher as your last called middleware.
     * Otherwise, how will your commands be distributed to their matching handlers?
     */
    class ExampleFactory {

        private final List<CommandBusMiddleware> middlewares;

        /**
         * Feel free to add as many middleware as you want
         */
        public ExampleFactory(ExecutionTimeMiddleware executionTimeMiddleware, DispatcherMiddleware dispatcherMiddleware) {
            this(asList(executionTimeMiddleware, dispatcherMiddleware));
        }

        /**
         * This should totally be injected by you dependency injection tool. See examples in other modules.
         */
        public ExampleFactory(List<CommandBusMiddleware> middlewares) {
            this.middlewares = middlewares;
        }

        /**
         * A really simple bus.
         */
        public CommandBus simpleCommandBus() {

            Chain chainOfMiddleware = new Chain.Factory().chainOfMiddleware(middlewares);

            return new CommandBus() {
                @Override
                public <T> CommandResponse<T> dispatch(Command<T> command) {
                    return chainOfMiddleware.handle(command);
                }
            };
        }

        /**
         * If you prefer the functional style. It is the same bus as above really.
         */
        public CommandBus simpleCommandBusFunctional() {
            Chain chainOfMiddleware = new Chain.Factory().chainOfMiddleware(middlewares);
            return chainOfMiddleware::handle;
        }

        /**
         * A bus filtering (and only accepting to process) commands that also implement the Serializable interface.
         * Not sure why one would want to do that but it makes for an example.
         */
        public CommandBus simpleFilteringCommandBus() {
            Chain chainOfMiddleware = new Chain.Factory().chainOfMiddleware(
                    Stream.concat(
                            Stream.of(new FilteringMiddleware<>(Serializable.class)),
                            middlewares.stream())
                            .collect(Collectors.toList())
            );
            return chainOfMiddleware::handle;
        }
    }

}
