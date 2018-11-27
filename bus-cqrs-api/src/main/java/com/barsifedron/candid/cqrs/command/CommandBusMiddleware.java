package com.barsifedron.candid.cqrs.command;

/**
 * A really simple command interceptor interface.
 * <p>
 * We'll use this to create mighty chains of decorators!
 */
public interface CommandBusMiddleware {

    <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next);

}
