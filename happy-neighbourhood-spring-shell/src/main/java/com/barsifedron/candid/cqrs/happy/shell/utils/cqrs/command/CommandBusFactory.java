package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.MapCommandBus;
import com.barsifedron.candid.cqrs.command.middleware.DomainEventsDispatcher;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;
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
     * A simple bus.
     * The work done by the command handlers and the event handlers happens in the SAME transaction.
     */
    public CommandBus simpleBus() {

        CommandBusMiddleware transactionalMiddleware = transactionalMiddleware();

        DomainEventBus domainEventBus = DomainEventBusMiddleware
                .compositeOf(
                        new ExecutionTimeLoggingDomainEventsBusMiddleware(),
                        new DomainEventBusInfoMiddleware("" +
                                "\n Entering main transaction domain event bus." +
                                "\n Warning : The event handlers execute in the same transaction as the command."))
                .decorate(domainEventBusFactory.basicEventBus());

        return CommandBusMiddleware
                .compositeOf(

                        new WithErrorLogCommandBusMiddleware(),
                        new WithExecutionDurationLogging(),
                        new DetailedLoggingCommandBusMiddleware(),
                        new ValidatingCommandBusMiddleware(),

                        // transactional boundary
                        transactionalMiddleware,

                        // events handlers executed within the main transaction
                        new DomainEventsDispatcher(domainEventBus))

                .decorate(new MapCommandBus(commandHandlersRegistry.handlers()));
    }

    /**
     * A more complex wiring of the command bus to illustrate more advanced behaviors.
     * This is similar to what I use in my own projects.
     * <p>
     * By default, the work done by the command handlers and the event handlers happens in the SAME transaction.
     * Perfect if a failure at any point should revert the whole operation.
     * I find this to be ok in most situations.
     * <p>
     * But you might want specific events be processed in their own separate transactions.
     * For example, if you want to make sure entities are persisted before calling an external
     * service or if failing to send an email should not compromise what was otherwise successful.
     * For this, your domain event HANDLERS just need to implement the following specific marker interface :
     *
     * @see com.barsifedron.candid.cqrs.spring.domainevent.ToProcessAfterMainTransaction
     * <p>
     * In that case, handlers implementing this interface will be executed in a separate transaction than the main one.
     * <p>
     * Warning 1 : since domain events can have many handlers, for the SAME domain event,
     * some of its handlers can proceed within the main transaction AND others can proceed outside.
     * <p>
     * Warning 2 : The use of the marker interface will only work if you bus if properly configured for it.
     * AKA : Using it with the simple bus above
     * @see CommandBusFactory#simpleBus()
     * <p>
     * would not change the behavior and all handlers would still be processed in the main transaction.
     * <p>
     * For an example look here and how these are processed in the logs:
     * @see com.barsifedron.candid.cqrs.happy.domainevents.ItemBorrowedDomainEventHandler_InMainTransaction
     * @see com.barsifedron.candid.cqrs.happy.domainevents.ItemBorrowedDomainEventHandler_PostMainTransaction
     */
    public CommandBus withOutsideTransactionCapability() {

        CommandBusMiddleware transactionalMiddleware = transactionalMiddleware();

        return CommandBusMiddleware
                .compositeOf(

                        new WithErrorLogCommandBusMiddleware(),
                        new WithExecutionDurationLogging(),
                        new DetailedLoggingCommandBusMiddleware(),
                        new ValidatingCommandBusMiddleware(),

                        // events handlers executed after the main transaction
                        new DomainEventsDispatcher(afterMainTransactionDomainEventBus()),

                        // transactional boundary
                        transactionalMiddleware,

                        // events handlers executed within the main transaction
                        new DomainEventsDispatcher(inMainTransactionDomainEventBus()))

                .decorate(new MapCommandBus(commandHandlersRegistry.handlers()));
    }

    /**
     * @TODO I leave this one to you as an exercise. The bottom is a minimal working bus.
     * Try to customize it to your taste and call it from a controller to see changes
     */
    public CommandBus yourCustomCommandBus() {
        return CommandBusMiddleware
                .compositeOf(
                        CommandBusMiddleware.neutral(),

                        // add your command middlewares here
                        // ...

                        // Also try to customize the domain event bus
                        new DomainEventsDispatcher(yourCustomDomainEventBus())
                )
                .decorate(new MapCommandBus(commandHandlersRegistry.handlers()));
    }

    /**
     * @TODO see above method
     */
    private DomainEventBus yourCustomDomainEventBus() {
        return DomainEventBusMiddleware
                .compositeOf(

                        DomainEventBusMiddleware.neutral()

                        // add your domain events middlewares here
                        // ...
                )
                .decorate(domainEventBusFactory.basicEventBus());
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
                                "\n Warning : The event handlers execute in the SAME transaction as the command."),
                        transactionalEventBusMiddleware())
                .decorate(baseEventBus);
    }

    /**
     * This bus only includes and executes event handlers marked WITH :
     *
     * @see com.barsifedron.candid.cqrs.spring.domainevent.ToProcessAfterMainTransaction
     */
    public DomainEventBus afterMainTransactionDomainEventBus() {
        DomainEventBus baseBus = domainEventBusFactory.afterMainTransactionDomainEventBus();
        return DomainEventBusMiddleware
                .compositeOf(
                        new ExecutionTimeLoggingDomainEventsBusMiddleware(),
                        new DomainEventBusInfoMiddleware("" +
                                "\n Entering AFTER main transaction domain event bus. " +
                                "\n Warning : Event handlers executes in a SEPARATE transactional unit than the command."),
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
