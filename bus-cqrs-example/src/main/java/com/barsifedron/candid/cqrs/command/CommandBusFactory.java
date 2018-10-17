package com.barsifedron.candid.cqrs.command;


import com.barsifedron.candid.cqrs.command.middleware.WithExecutionDurationLogging;
import com.barsifedron.candid.cqrs.command.middleware.WithFilteringByCommandType;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;

import java.io.Serializable;
import java.util.Set;

/**
 * A Simple example factory. more advanced examples are provided in the other modules
 */
public class CommandBusFactory {

    private final DomainEventBus eventBus;
    private final Set<CommandHandler> commandHandlers;

    /**
     * This should totally be injected by you dependency injection tool. See examples in other modules.
     */
    public CommandBusFactory(DomainEventBus eventBus, Set<CommandHandler> commandHandlers) {
        this.eventBus = eventBus;
        this.commandHandlers = commandHandlers;
    }

    /**
     * Keep wrapping them with other decorators to add behavior.
     * If it becomes too tedious (it is not really imho), why not look at the other way to wrap comand bus middlewares
     * that is in the separate bus-cqs module. It should be easy for you to adapt.
     */
    public CommandBus newSimpleCommandBus() {
        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new WithExecutionDurationLogging(),
                new CommandBusMiddleware.EventBusDispatcherMiddleware(eventBus),
                new CommandBusMiddleware.Dispatcher(commandHandlers));
        return chain::dispatch;
    }

    /**
     * A bus, filtering (and only accepting to process) commands that also implement the Serializable interface.
     * Not sure why one would want to do that but it makes for an example of wrapping the command buses to obtain complex behaviors.
     */
    public CommandBus newSimpleFilteringCommandBus() {
        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new WithExecutionDurationLogging(),
                new WithFilteringByCommandType(Serializable.class),
                new CommandBusMiddleware.EventBusDispatcherMiddleware(eventBus),
                new CommandBusMiddleware.Dispatcher(commandHandlers)
        );
        return chain::dispatch;
    }

}


