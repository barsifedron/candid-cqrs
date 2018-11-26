package com.barsifedron.candid.cqrs.command;


import com.barsifedron.candid.cqrs.command.middleware.CommandBusDispatcher;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * A command bus made of a chain of middlewares.
 * <p>
 * This uses the chain of responsibility pattern, aka, infinite interceptors...
 * <p>
 * Think of this as an "horizontal" chains of decorators. Each one adds behaviour before passing
 * the command to the next guy in the chain.
 * <p>
 * This is an extremely powerful way to add common behavior to all your command processing.
 * Simple examples of middleware:
 * <ul>
 * <li> A middleware opening and closing transactions around your command handling.</li>
 * <li> A middleware logging the execution time taken to process your command.</li>
 * <li> A middleware storing all the processed commands for audit later ect... </li>
 * </ul>
 * <p>
 * To help you understand, a few examples are provided in the bus-cqrs-example project
 * We chose this construct as it will make the creation of the command bus more clear and cleaner.
 * But you can use a normal CommandBus decorator if you prefer. Both are valid choices.
 * <p>
 * There are more concise ways to wire the bus.
 */
public class CommandBusMiddlewareChain implements CommandBus {

    private final CommandBusMiddleware middleware;
    private final CommandBusMiddlewareChain nextInChain;

    public CommandBusMiddlewareChain(CommandBusMiddleware middleware, CommandBusMiddlewareChain nextInChain) {
        this.middleware = middleware;
        this.nextInChain = nextInChain;
    }

    /**
     * This will recursively pass the command through the chain of middleware.
     * Each middleware will intercept the command. Once it is done with the command, the middleware will hand it to its successor in the chain.
     * Although the wiring is less common, it is exactly the same thing as a decorator.
     */
    @Override
    public <T> CommandResponse<T> dispatch(Command<T> command) {
        return middleware.dispatch(command, nextInChain);
    }

    <T extends CommandBusMiddleware> boolean usesMiddlewareInstanceOf(Class<T> middlewareClass) {
        if (middleware == null) {
            return false;
        }
        if (middleware.getClass() == middlewareClass) {
            return true;
        }
        if (nextInChain == null) {
            return false;
        }
        return nextInChain.usesMiddlewareInstanceOf(middlewareClass);
    }


    public static class Factory {

        public CommandBusMiddlewareChain chain(CommandBusMiddleware... middlewares) {
            List<CommandBusMiddleware> list = Stream.of(middlewares).collect(toList());
            return chain(list);
        }

        /**
         * From a list of middleware, creates a Chain, wrapping them recursively into each others.
         * The "last" middleware called is always be the dispatcher and, contrarily to the others,
         * will not forward the command but finally handle it to the command handlers.
         * Hence the last Chain built with "null". You are better than me and can probably find a more elegant way to do this.
         */
        public CommandBusMiddlewareChain chain(List<CommandBusMiddleware> middlewares) {
            validate(middlewares);
            if (middlewares.size() == 1) {
                return new CommandBusMiddlewareChain(middlewares.get(0), unreachableNextInChain());
            }
            return new CommandBusMiddlewareChain(
                    middlewares.get(0),
                    chain(middlewares.subList(1, middlewares.size())));
        }

        private void validate(List<CommandBusMiddleware> middlewares) {
            if (middlewares == null) {
                throw new RuntimeException("Can not create a middleware chain from a null list of middlewares");
            }
            if (middlewares.isEmpty()) {
                throw new RuntimeException("Can not operate on an empty list of middlewares");
            }
            if (middlewares.stream().anyMatch(Objects::isNull)) {
                throw new RuntimeException("Can not accept a null middleware in the lists of middlewares");
            }
            CommandBusMiddleware lastMiddlewareInChain = middlewares.get(middlewares.size() - 1);
            if (!lastMiddlewareInChain.getClass().isAssignableFrom(CommandBusDispatcher.class)) {
                throw new RuntimeException("The last middleware of the chain must always be the one dispatching to handlers.");
            }
        }

        private CommandBusMiddlewareChain unreachableNextInChain() {
            CommandBusMiddleware unreachableMiddleware = new CommandBusMiddleware() {
                @Override
                public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
                    throw new IllegalArgumentException("This should never be called");
                }
            };
            return new CommandBusMiddlewareChain(unreachableMiddleware, null);
        }
    }

}
