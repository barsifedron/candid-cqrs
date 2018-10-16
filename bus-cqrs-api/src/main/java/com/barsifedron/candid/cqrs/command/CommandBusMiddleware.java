package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

/**
 * The middleware intercept your cammand on its way to or back from the command handlers.
 * Think of this as a chain of decorators, each one adding its own behaviour to the process.
 * <p>
 * This is an extremely powerful way to add common behavior to all your command processing.
 * Simple examples of middleware:
 * A middleware opening and closing transactions around your cammand handling.
 * A middleware logging the execution time taken to process your command etc...
 * <p>
 * To help you understand, a few examples are provided in the bus-cqrs-example project
 * <p>
 * See an alternative way of doing this in the `bus-cqs` module.
 */
public interface CommandBusMiddleware {

    <T> CommandResponse<T> dispatch(Command<T> command, CommandBusMiddlewareChain next);

    /**
     * This is in charge of dispatching the command to the right Command Handler.
     * This will only allow for one handler per command type. As it should be.
     */
    class Dispatcher implements CommandBusMiddleware {

        private final Map<Class, CommandHandler> handlers;

        /**
         * The set of handlers will usually be injected by your dependency injection tool.
         * Examples for this can be found in the other modules.
         */
        public Dispatcher(Set<? extends CommandHandler> commandHandlers) {
            this(commandHandlers.stream().collect(
                    toMap(
                            handler -> handler.listenTo(),
                            handler -> handler)));
        }

        public Dispatcher(Map<Class, CommandHandler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public <T> CommandResponse<T> dispatch(Command<T> command, CommandBusMiddlewareChain unreachableChain) {

            CommandHandler<T, Command<T>> handler = Optional
                    .ofNullable(command)
                    .map(Command::getClass)
                    .map(commandType -> handlers.get(commandType))
                    .orElseThrow(() -> new CommandHandlerNotFoundException(command.getClass()));
            return handler.handle(command);

        }

    }


    /**
     * Your command handlers may return events (For example UserPhoneNumberUpdated).
     * This middleware is in charge of dispatching those events to their right full event handlers.
     * Those handlers can in turn react to these events to do many things such as updating counters,
     * updating database projections for your read side or plan to communicate new facts to the outside world.
     * Beware : The events listeners we refer to here are local events listeners. these events should not be
     * propagated to kafka or aws from the event listener.
     * <p>
     * Sending communications directly in an event listener is a dangerous practice as you could
     * send an email and then see the transaction commit fail.
     * Which would lead to un synchronized states between the current bounded context and the external world.
     * A better way would be to store your message/email within the same transaction and have
     * another process be in charge of processing those. It could be triggered by a cron job.
     * <p>
     * Following good practices all this should happen within the same command/write transaction.
     * And our transaction should include :
     * <ul>
     * <li>New aggregate state persistence</li>
     * <li>Projections or other things affecting the database</li>
     * <li>Your "intent" to communicate changes to the outside world</li>
     * <ul/>
     * <p>
     */
    class EventBusDispatcherMiddleware implements CommandBusMiddleware {

        private final DomainEventBus eventBus;

        public EventBusDispatcherMiddleware(DomainEventBus eventBus) {
            this.eventBus = eventBus;
        }

        @Override
        public <T> CommandResponse<T> dispatch(Command<T> command, CommandBusMiddlewareChain next) {
            CommandResponse<T> response = next.dispatch(command);
            response.events.forEach(eventBus::dispatch);
            return response;
        }
    }


    class CommandHandlerNotFoundException extends RuntimeException {
        public CommandHandlerNotFoundException(Class<? extends Command> aClass) {
            super("Could not find Command Handler for command of type " + aClass.getName());
        }
    }


}
