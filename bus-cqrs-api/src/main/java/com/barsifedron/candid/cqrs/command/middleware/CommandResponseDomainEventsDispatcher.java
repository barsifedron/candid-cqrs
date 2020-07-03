package com.barsifedron.candid.cqrs.command.middleware;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;

/**
 * Your command handlers may return events (For example UserPhoneNumberUpdated).
 * This middleware is in charge of dispatching those events to the event bus, which will deliver the to their right full event handlers.
 * <p>
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
public class CommandResponseDomainEventsDispatcher implements CommandBusMiddleware {

    private final DomainEventBus eventBus;

    public CommandResponseDomainEventsDispatcher(DomainEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus bus) {
        CommandResponse<T> response = bus.dispatch(command);
        response.domainEvents.forEach(eventBus::dispatch);
        return response;
    }
}
