package com.barsifedron.candid.cqrs.command;


import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Chain of responsibility pattern, aka, infinite interceptors... Will allow us
 * to dynamically create chains of middleware.
 */
public class CommandBusMiddlewareChain {

    private final CommandBusMiddleware middleware;
    private final CommandBusMiddlewareChain nextInChain;

    public CommandBusMiddlewareChain(CommandBusMiddleware middleware, CommandBusMiddlewareChain next) {
        this.middleware = middleware;
        this.nextInChain = next;
    }

    public <T> CommandResponse<T> dispatch(Command<T> command) {
        return middleware.dispatch(command, nextInChain);
    }


    <T extends CommandBusMiddleware> boolean contains(Class<T> middlewareClass) {

        if (middleware == null) {
            return false;
        }
        if (middleware.getClass() == middlewareClass) {
            return true;
        }
        if (nextInChain == null) {
            return false;
        }
        return nextInChain.contains(middlewareClass);

    }


    public static class Factory {

        /**
         * From a list of middleware, creates a Chain, wrapping them recursively into each others.
         * The "last" middleware called should be the dispatcher and, contrarily to the others, will not forward the command put finally handle it to the wanted handlers.
         * Hence the last Chain built with "null". You are better than me and can probably find a more safe and elegant way to do this.
         */
        public CommandBusMiddlewareChain chainOfMiddleware(List<CommandBusMiddleware> middlewares) {

            validate(middlewares);
            if (middlewares.size() == 1) {
                return new CommandBusMiddlewareChain(middlewares.get(0), unreachableNextInChain());
            }
            return new CommandBusMiddlewareChain(
                    middlewares.get(0),
                    chainOfMiddleware(middlewares.subList(1, middlewares.size())));

        }

        public CommandBusMiddlewareChain chainOfMiddleware(CommandBusMiddleware... middlewares) {
            return chainOfMiddleware(Stream.of(middlewares).collect(toList()));
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
            CommandBusMiddleware lastMiddlewareInChain = middlewares.stream().reduce((first, last) -> last).get();
            validateLastMiddleware(lastMiddlewareInChain);
        }

        private void validateLastMiddleware(CommandBusMiddleware commandBusMiddleware) {
            if (!commandBusMiddleware.getClass().isInstance(CommandBusMiddleware.Dispatcher.class)) {
                throw new RuntimeException("The last middleware of the chain must always be the one dispatching to handlers.");
            }
        }

        private CommandBusMiddlewareChain unreachableNextInChain() {

            CommandBusMiddleware unreachableMiddleware = new CommandBusMiddleware() {
                @Override
                public <T> CommandResponse<T> dispatch(Command<T> command, CommandBusMiddlewareChain next) {
                    throw new IllegalArgumentException("This should never be called");
                }
            };
            return new CommandBusMiddlewareChain(unreachableMiddleware, null);

        }
    }


}
