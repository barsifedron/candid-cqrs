package com.barsifedron.candid.cqrs.springboot.cqrs.command;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.middleware.*;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;
import com.barsifedron.candid.cqrs.domainevent.middleware.DomainEventBusDispatcher;
import com.barsifedron.candid.cqrs.spring.CommandHandlersRegistry;
import com.barsifedron.candid.cqrs.spring.DomainEventHandlersRegistry;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

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

        DomainEventBus domainEventBus = DomainEventBusMiddleware
                .chainManyIntoADomainEventBus(new DomainEventBusDispatcher(domainEventHandlersRegistry.handlers()));

        return CommandBusMiddleware.chainManyIntoACommandBus(
                new WithExecutionDurationLogging(),
                new DetailedLoggingCommandBusMiddleware(),
                new ValidatingCommandBusMiddleware(),
                transactionalMiddleware,
                new CommandResponseDomainEventsDispatcher(domainEventBus),
                new CommandBusDispatcher(commandHandlersRegistry.handlers())
        );
    }

    private CommandBusMiddleware transactionalMiddleware() {
        return transactionalCommandBusMiddleware::runInTransaction;
    }

}
