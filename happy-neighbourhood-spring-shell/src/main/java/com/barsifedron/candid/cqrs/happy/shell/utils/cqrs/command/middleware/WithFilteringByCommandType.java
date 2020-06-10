package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command.middleware;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.CommandResponse;

/**
 * For the sake of providing an example of a decorating function:
 * A decorator filtering commands, and only processing them if they implement a certain interface.
 */
public class WithFilteringByCommandType<V> implements CommandBusMiddleware {

    private final Class<? extends V> filteringClass;

    public WithFilteringByCommandType(Class<? extends V> filteringClass) {
        this.filteringClass = filteringClass;
    }

    public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
        boolean shouldProcessCommand = command.getClass().isInstance(filteringClass);
        return shouldProcessCommand
                ? next.dispatch(command)
                : new CommandResponse<>(null); // you can do better than a null here
    }
}

