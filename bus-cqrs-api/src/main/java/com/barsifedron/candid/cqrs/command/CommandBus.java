package com.barsifedron.candid.cqrs.command;


/**
 * A really simple command bus interface.
 */
@FunctionalInterface
public interface CommandBus {

    <T> CommandResponse<T> dispatch(Command<T> command);

}