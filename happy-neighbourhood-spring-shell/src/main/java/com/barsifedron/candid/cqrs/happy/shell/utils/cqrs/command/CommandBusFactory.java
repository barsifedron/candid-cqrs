package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.MapCommandBus;
import com.barsifedron.candid.cqrs.command.middleware.DomainEventsDispatcher;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;
import com.barsifedron.candid.cqrs.domainevent.ToProcessAfterMainTransaction;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command.middleware.WithErrorLogCommandBusMiddleware;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command.middleware.WithExecutionDurationLogging;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.domainevents.DomainEventBusFactory;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.domainevents.middleware.DomainEventBusInfoMiddleware;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.domainevents.middleware.ExecutionTimeLoggingDomainEventsBusMiddleware;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.domainevents.middleware.TransactionalDomainEventBusMiddleware;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.middleware.DetailedLoggingCommandBusMiddleware;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.middleware.ValidatingCommandBusMiddleware;
import com.barsifedron.candid.cqrs.spring.CommandHandlersRegistry;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CommandBusFactory {

    private DomainEventBusFactory domainEventBusFactory;
    private CommandHandlersRegistry commandHandlersRegistry;
    private TransactionalCommandBusMiddleware transactionalCommandBusMiddleware;
    private TransactionalDomainEventBusMiddleware transactionalDomainEventBusMiddleware;

    @Inject
    public CommandBusFactory(
            ApplicationContext applicationContext,
            DomainEventBusFactory domainEventBusFactory,
            TransactionalCommandBusMiddleware transactionalCommandBusMiddleware,
            TransactionalDomainEventBusMiddleware transactionalDomainEventBusMiddleware) {

        this.domainEventBusFactory = domainEventBusFactory;

        this.commandHandlersRegistry = new CommandHandlersRegistry(
                applicationContext,
                "com.barsifedron.candid.cqrs.happy",
                "com.barsifedron.candid.cqrs.springboot.app");

        this.transactionalCommandBusMiddleware = transactionalCommandBusMiddleware;
        this.transactionalDomainEventBusMiddleware = transactionalDomainEventBusMiddleware;
    }

    /**
     * A simple bus. All domain events are processed within the main transaction.
     */
    public CommandBus simpleBus() {

        CommandBusMiddleware transactionalMiddleware = transactionalMiddleware();

        DomainEventBus domainEventBus = domainEventBusFactory.withAllHandlersEventBus();

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

    /**
     * By default, the work done by the command handlers and the event handlers happens in the SAME transaction.
     * Perfect if a failure at any point should revert the whole operation. I find this to be ok in most situations.
     * <p>
     * But you might want specific events be processed in their own separate transactions.
     * For example, if you want to make sure entities are persisted before calling an external
     * service or if failing to send an email should not compromise what was otherwise successful.
     * For this, your domain event handler just need to implement the following specific marker interface :
     *
     * @see com.barsifedron.candid.cqrs.spring.domainevent.ToProcessAfterMainTransaction
     * <p>
     * In that case, handlers implementing this interface will be executed in a separate transaction than the main one.
     * <p>
     * Warning : since domain events can have many handlers, for the SAME domain event,
     * some of its handlers can proceed within the main transaction AND others can proceed outside.
     */
    public CommandBus complexBus() {

        CommandBusMiddleware transactionalMiddleware = transactionalMiddleware();

        return CommandBusMiddleware
                .compositeOf(

                        new WithErrorLogCommandBusMiddleware(),
                        new WithExecutionDurationLogging(),
                        new DetailedLoggingCommandBusMiddleware(),
                        new ValidatingCommandBusMiddleware(),

                        // events processed after the main transaction
                        new DomainEventsDispatcher(afterMainTransactionDomainEventBus()),

                        // transactional boundary
                        transactionalMiddleware,

                        // events processed within the main transaction
                        new DomainEventsDispatcher(inMainTransactionDomainEventBus()))

                .decorate(new MapCommandBus(commandHandlersRegistry.handlers()));
    }


    /**
     * This bus only includes and executes event handlers NOT marked with :
     *
     * @see com.barsifedron.candid.cqrs.spring.domainevent.ToProcessAfterMainTransaction
     */
    public DomainEventBus inMainTransactionDomainEventBus() {
        DomainEventBus baseEventBus = domainEventBusFactory.inMainTransactionDomainEventBus();
        return DomainEventBusMiddleware
                .compositeOf(
                        new ExecutionTimeLoggingDomainEventsBusMiddleware(),
                        new DomainEventBusInfoMiddleware("" +
                                "\n Entering main transaction domain event bus." +
                                "\n Warning : The event handlers execute in the same transaction as the command."),
                        transactionalEventBusMiddleware())
                .decorate(baseEventBus);
    }

    /**
     * This bus only includes and executes event handlers marked WITH :
     *
     * @see ToProcessAfterMainTransaction
     */
    public DomainEventBus afterMainTransactionDomainEventBus() {
        DomainEventBus baseBus = domainEventBusFactory.afterMainTransactionDomainEventBus();
        return DomainEventBusMiddleware
                .compositeOf(
                        new ExecutionTimeLoggingDomainEventsBusMiddleware(),
                        new DomainEventBusInfoMiddleware("" +
                                "\n Entering AFTER main transaction domain event bus. " +
                                "\n Warning : Event handlers executes in a separate transactional unit than the main one."),
                        transactionalEventBusMiddleware())
                .decorate(baseBus);
    }


    private CommandBusMiddleware transactionalMiddleware() {
        return transactionalCommandBusMiddleware::runInTransaction;
    }

    private DomainEventBusMiddleware transactionalEventBusMiddleware() {
        return transactionalDomainEventBusMiddleware::runInTransaction;
    }


}
