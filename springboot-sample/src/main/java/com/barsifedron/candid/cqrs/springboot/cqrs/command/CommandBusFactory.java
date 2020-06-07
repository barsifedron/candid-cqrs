package com.barsifedron.candid.cqrs.springboot.cqrs.command;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.middleware.*;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;
import com.barsifedron.candid.cqrs.domainevent.middleware.DomainEventBusDispatcher;
import com.barsifedron.candid.cqrs.spring.CommandHandlersRegistry;
import com.barsifedron.candid.cqrs.spring.CommandHandlersRegistryNoAnnotation;
import com.barsifedron.candid.cqrs.spring.DomainEventHandlersRegistry;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CommandBusFactory {

    private CommandHandlersRegistry commandHandlersRegistry;
    private DomainEventHandlersRegistry domainEventHandlersRegistry;

    @Inject
    public CommandBusFactory(
            ApplicationContext applicationContext,
            DomainEventHandlersRegistry domainEventHandlersRegistry) {

        this.domainEventHandlersRegistry = new DomainEventHandlersRegistry(
                applicationContext,
                "com.barsifedron.candid.cqrs.springboot.app");

        this.commandHandlersRegistry = new CommandHandlersRegistry(
                applicationContext,
                "com.barsifedron.candid.cqrs.springboot.app");
    }

    public CommandBus simpleBus() {

        DomainEventBus domainEventBus = DomainEventBusMiddleware
                .chainManyIntoADomainEventBus(new DomainEventBusDispatcher(domainEventHandlersRegistry.handlers()));

        return CommandBusMiddleware.chainManyIntoACommandBus(
                new WithExecutionDurationLogging(),
                new DetailedLoggingCommandBusMiddleware(),
                new ValidatingCommandBusMiddleware(),
                new CommandResponseDomainEventsDispatcher(domainEventBus),
                new CommandBusDispatcher(commandHandlersRegistry.handlers())
        );
    }
}
