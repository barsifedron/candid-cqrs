package com.barsifedron.candid.cqrs.springboot;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.middleware.*;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;
import com.barsifedron.candid.cqrs.domainevent.middleware.DomainEventBusDispatcher;
import com.barsifedron.candid.cqrs.spring.CommandHandlersRegistry;
import com.barsifedron.candid.cqrs.spring.DomainEventHandlersRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandBusFactory {

    private CommandHandlersRegistry commandHandlersRegistry;
    private DomainEventHandlersRegistry domainEventHandlersRegistry;

    @Autowired
    public CommandBusFactory(
            CommandHandlersRegistry commandHandlersRegistry,
            DomainEventHandlersRegistry domainEventHandlersRegistry) {
        this.commandHandlersRegistry = commandHandlersRegistry;
        this.domainEventHandlersRegistry = domainEventHandlersRegistry;
    }

    public CommandBus simpleBus() {

        DomainEventBus domainEventBus = DomainEventBusMiddleware.chainManyIntoADomainEventBus(new DomainEventBusDispatcher(domainEventHandlersRegistry.handlers()));

        return CommandBusMiddleware.chainManyIntoACommandBus(
                new WithExecutionDurationLogging(),
                new DetailedLoggingCommandBusMiddleware(),
                new ValidatingCommandBusMiddleware(),
                new CommandResponseDomainEventsDispatcher(domainEventBus),
                new CommandBusDispatcher(commandHandlersRegistry.handlers())
        );
    }
}
