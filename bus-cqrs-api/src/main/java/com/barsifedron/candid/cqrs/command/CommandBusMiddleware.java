package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.command.middleware.CommandBusDispatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A really simple command interceptor interface.
 * We'll use this to create mighty chains of decorators!
 */
public interface CommandBusMiddleware {

    <T> CommandResponse<T> dispatch(Command<T> command, CommandBus bus);

    /**
     * Decorates a command bus with this middleware.
     */
    default CommandBus decorate(CommandBus bus) {
        CommandBusMiddleware thisMiddleware = this;
        return new CommandBus() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command) {
                return thisMiddleware.dispatch(command, bus);
            }
        };
    }

    /**
     * Decorates an existing middleware with this middleware.
     */
    default CommandBusMiddleware decorate(CommandBusMiddleware middleware) {
        CommandBusMiddleware thisMiddleware = this;
        return new CommandBusMiddleware() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus bus) {
                CommandBus decoratedBus = middleware.decorate(bus);
                return thisMiddleware.dispatch(command, decoratedBus);
            }
        };
    }

    static CommandBus chainManyIntoACommandBus(CommandBusMiddleware... middlewares) {
        return Chain.manyIntoACommandBus(Arrays.asList(middlewares));
    }

    static CommandBusMiddleware chainManyIntoACommandBusMiddleware(CommandBusMiddleware... middlewares) {
        return Chain.manyIntoACommandBusMiddleware(Arrays.asList(middlewares));
    }

    class Chain {

        /**
         * Creates a command bus from a list of middleware, wrapping them recursively into each others.
         * The "last" middleware called must ALWAYS be the dispatcher and, contrarily to the others,
         * will not forward the command but finally handle it to the command handlers.
         * Hence the last Chain built with "null".
         */
        static CommandBus manyIntoACommandBus(List<CommandBusMiddleware> middlewares) {
            validate(middlewares);
            validateLastMiddlewareIsDispatcher(middlewares);
            CommandBusMiddleware compositeMiddleware = manyIntoACommandBusMiddleware(middlewares);
            return compositeMiddleware.decorate((CommandBus) null);
        }

        /**
         * Wraps a list of middleware into each other to create a composite one.
         */
        static CommandBusMiddleware manyIntoACommandBusMiddleware(List<CommandBusMiddleware> middlewares) {
            validate(middlewares);
            if (middlewares.size() == 1) {
                return middlewares.get(0);
            }
            return middlewares.get(0)
                    .decorate(manyIntoACommandBusMiddleware(middlewares.subList(1, middlewares.size())));
        }

        private static void validate(List<CommandBusMiddleware> middlewares) {
            if (middlewares == null) {
                throw new RuntimeException("Can not create a middleware chain from a null list of middlewares");
            }
            if (middlewares.isEmpty()) {
                throw new RuntimeException("Can not operate on an empty list of middlewares");
            }
            if (middlewares.stream().anyMatch(Objects::isNull)) {
                throw new RuntimeException("Can not accept a null middleware in the lists of middlewares");
            }
        }

        private static void validateLastMiddlewareIsDispatcher(List<CommandBusMiddleware> middlewares) {
            CommandBusMiddleware lastMiddlewareInChain = middlewares.get(middlewares.size() - 1);
            if (!lastMiddlewareInChain.getClass().isAssignableFrom(CommandBusDispatcher.class)) {
                throw new RuntimeException(
                        "The last middleware of the chain must always be the one dispatching to handlers.");
            }
        }
    }

}
