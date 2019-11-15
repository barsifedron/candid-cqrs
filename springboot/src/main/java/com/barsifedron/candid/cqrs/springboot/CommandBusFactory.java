package com.barsifedron.candid.cqrs.springboot;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddlewareChain;
import com.barsifedron.candid.cqrs.command.middleware.*;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddlewareChain;
import com.barsifedron.candid.cqrs.domainevent.middleware.DomainEventBusDispatcher;
import com.barsifedron.candid.cqrs.spring.CommandHandlersRegistry;
import com.barsifedron.candid.cqrs.spring.DomainEventHandlersRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

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

        DomainEventBusMiddlewareChain domainEventBus = new DomainEventBusMiddlewareChain.Factory().chainOfMiddleware(
                new DomainEventBusDispatcher(domainEventHandlersRegistry.handlers())
        );

        return new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new WithExecutionDurationLogging(),
                new DetailedLoggingCommandBusMiddleware(),
                new ValidatingCommandBusMiddleware(),
                new DomainEventsDispatcher(domainEventBus),
                new CommandBusDispatcher(commandHandlersRegistry.handlers())
        );
    }
}
