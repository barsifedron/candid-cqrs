package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.command.middleware.CommandBusDispatcher;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Creates a command bus from a list of middleware, wrapping them recursively into each others.
 * The "last" middleware called is always be the dispatcher and, contrarily to the others,
 * will not forward the command but finally handle it to the command handlers.
 * Hence the last Chain built with "null".
 */
public class WiredCommandBus {

    public static CommandBus of(CommandBusMiddleware... middlewares) {
        List<CommandBusMiddleware> list = Stream.of(middlewares).collect(toList());
        return of(list);
    }

    public static CommandBus of(List<CommandBusMiddleware> middlewares) {
        validate(middlewares);
        if (middlewares.size() == 1) {
            return middlewares.get(0).wrap((CommandBus) null);
        }
        return middlewares.get(0).wrap(of(middlewares.subList(1, middlewares.size())));
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
        CommandBusMiddleware lastMiddlewareInChain = middlewares.get(middlewares.size() - 1);
        if (!lastMiddlewareInChain.getClass().isAssignableFrom(CommandBusDispatcher.class)) {
            throw new RuntimeException(
                    "The last middleware of the chain must always be the one dispatching to handlers.");
        }
    }

}
