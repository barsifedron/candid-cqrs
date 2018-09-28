package com.barsifedron.candid.cqs.command.middleware;

import com.barsifedron.candid.cqs.command.SimpleCommand;

import java.util.List;

/**
 * Chain of responsibility pattern, aka, infinite interceptors... Will allow us
 * to dynamically create chains of middleware.
 */
public class Chain {

    private final CommandBusMiddleware middleware;
    private final Chain next;

    public Chain(CommandBusMiddleware middleware, Chain next) {
        this.middleware = middleware;
        this.next = next;
    }

    public <T> T handle(SimpleCommand<T> command) {
        return middleware.handle(command, next::handle);
    }


    public static class Factory {

        /**
         * From a list of middleware, creates a Chain, wrapping them recursively into each others.
         * The "last" middleware called should be the dispatcher and, contrarily to the others, will not forward the command.
         * Hence the last Chain built with "null". You are better than me and can probably find a more safe and elegant way to do this.
         */
        public Chain chainOfMiddleware(List<CommandBusMiddleware> middlewares) {
            if (middlewares.isEmpty()) {
                throw new RuntimeException("Can not operate on an empty list of middlewares");
            }
            if (middlewares.size() == 1) {
                validateLastMiddleware(middlewares.get(0));
                return new Chain(middlewares.get(0), new Chain(null, null));
            }
            // recursive
            return new Chain(
                    middlewares.get(0),
                    chainOfMiddleware(middlewares.subList(1, middlewares.size())));
        }


        private void validateLastMiddleware(CommandBusMiddleware commandBusMiddleware) {
            if (!commandBusMiddleware.getClass().isInstance(CommandBusMiddleware.DispatcherBusMiddleware.class)) {
                throw new RuntimeException("The last middleware of the chain must be the dispatcher one");
            }
        }
    }

}
