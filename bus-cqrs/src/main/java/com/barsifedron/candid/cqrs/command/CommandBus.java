package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.event.EventBus;

import java.io.Serializable;
import java.util.Set;

/**
 * A really simple command bus. Dispatches a command to its handler.
 * <p>
 * Inspired by the second part of this great presentation:
 * https://speakerdeck.com/lilobase/cqrs-fonctionnel-event-sourcing-and-domain-driven-design-breizhcamp-2017
 * <p>
 * See also : `https://www.slideshare.net/rosstuck/command-bus-to-awesome-town`
 */
public class CommandBus {

    private final EventBus eventBus;
    private final CommandBusMiddleware next;

    public CommandBus(EventBus eventBus, CommandBusMiddleware middleware) {
        this.eventBus = eventBus;
        this.next = middleware;
    }

    /**
     * Executes a command. Then, passes the generated events to the event bus before,
     * finally, returning the command result.
     * Does it mean that you have to wait for the events to be fully handled before returning the result?
     * Not necessarily.
     * It really depends on the type of event bus you implement and is totally up to you.
     */
    public <T> T dispatch(Command<T> command) {
        CommandResponse<T> response = next.dispatch(command);
        response.events.forEach(evt -> eventBus.dispatch(evt));
        return response.result;
    }

    /**
     * A Simple example factory. more advanced examples are provided in the other modules
     */
    class ExampleFactory {

        private final EventBus eventBus;
        private final Set<CommandHandler> commandHandlers;

        /**
         * This should totally be injected by you dependency injection tool. See examples in other modules.
         */
        public ExampleFactory(EventBus eventBus, Set<CommandHandler> commandHandlers) {
            this.eventBus = eventBus;
            this.commandHandlers = commandHandlers;
        }

        /**
         * Keep wrapping them with other decorators to add behavior.
         * If it becomes too tedious (it is not really imho), why not look at the other way to wrap comand bus middlewares
         * that is in the separate bus-cqs module. It should be easy for you to adapt.
         */
        public CommandBus newSimpleCommandBus() {
            return new CommandBus(
                    eventBus,
                    new CommandBusMiddleware.WithExecutionDurationLogging(
                            new CommandBusMiddleware.Dispatcher(commandHandlers)));
        }

        /**
         * A bus, filtering (and only accepting to process) commands that also implement the Serializable interface.
         * Not sure why one would want to do that but it makes for an example of wrapping the command buses to obtain complex behaviors.
         */
        public CommandBus newSimpleFilteringCommandBus() {
            return new CommandBus(
                    eventBus,
                    new CommandBusMiddleware.WithFilteringByCommandType(
                            Serializable.class,
                            new CommandBusMiddleware.WithExecutionDurationLogging(
                                    new CommandBusMiddleware.Dispatcher(commandHandlers))));
        }


    }
}