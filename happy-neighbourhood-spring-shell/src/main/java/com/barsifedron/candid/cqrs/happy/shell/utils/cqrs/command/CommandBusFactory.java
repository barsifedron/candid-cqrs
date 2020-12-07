package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.MapCommandBus;
import com.barsifedron.candid.cqrs.command.middleware.DomainEventsDispatcher;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;
import com.barsifedron.candid.cqrs.domainevent.MapDomainEventBus;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command.middleware.WithErrorLogCommandBusMiddleware;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command.middleware.WithExecutionDurationLogging;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.middleware.DetailedLoggingCommandBusMiddleware;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.middleware.ValidatingCommandBusMiddleware;
import com.barsifedron.candid.cqrs.spring.CommandHandlersRegistry;
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
    private TransactionalCommandBusMiddleware transactionalCommandBusMiddleware;

    @Inject
    public CommandBusFactory(
            ApplicationContext applicationContext,
            TransactionalCommandBusMiddleware transactionalCommandBusMiddleware) {

        this.domainEventHandlersRegistry = new DomainEventHandlersRegistry(
                applicationContext,
                "com.barsifedron.candid.cqrs.springboot.app",
                "com.barsifedron.candid.cqrs.happy.domainevents");

        this.commandHandlersRegistry = new CommandHandlersRegistry(
                applicationContext,
                "com.barsifedron.candid.cqrs.happy",
                "com.barsifedron.candid.cqrs.springboot.app");
        this.transactionalCommandBusMiddleware = transactionalCommandBusMiddleware;
    }

    public CommandBus simpleBus() {

        CommandBusMiddleware transactionalMiddleware = transactionalMiddleware();

        DomainEventBus domainEventBus = new MapDomainEventBus(domainEventHandlersRegistry.handlers());

        return CommandBusMiddleware
                .compositeOf(
                        new WithErrorLogCommandBusMiddleware(),
                        new WithExecutionDurationLogging(),
                        new DetailedLoggingCommandBusMiddleware(),
                        new ValidatingCommandBusMiddleware(),
                        transactionalMiddleware,
                        new DomainEventsDispatcher(domainEventBus))
                .decorate(new MapCommandBus(commandHandlersRegistry.handlers()));
    }

    private CommandBusMiddleware transactionalMiddleware() {
        return transactionalCommandBusMiddleware::runInTransaction;
    }

}
