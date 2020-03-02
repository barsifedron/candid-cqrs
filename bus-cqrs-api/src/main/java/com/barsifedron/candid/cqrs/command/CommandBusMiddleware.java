package com.barsifedron.candid.cqrs.command;

import java.util.function.Function;

/**
 * A really simple command interceptor interface.
 * <p>
 * We'll use this to create mighty chains of decorators!
 */
public interface CommandBusMiddleware {

    <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next);

    /**
     * Decorates a command bus with this middleware.
     */
    default CommandBus wrap(CommandBus bus) {

        Function<Command, CommandResponse> f = (command) -> this.dispatch(command, bus);
        CommandBus decoratedCommandBus = cmd -> f.apply(cmd);

//
//                CommandBus decoratedCommandBus = new CommandBus() {
//            @Override
//            public <T> CommandResponse<T> dispatch(Command<T> command) {
//                return f.apply(command);
//            }
//        };
        return decoratedCommandBus;
    }

    /**
     * Decorates an existing middleware with this middleware.
     */
    default CommandBusMiddleware wrap(CommandBusMiddleware middleware) {

        CommandBusMiddleware thisMiddleware = this;
        return new CommandBusMiddleware() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
                return thisMiddleware.dispatch(command, middleware.wrap(next));
            }
        };
    }
}
