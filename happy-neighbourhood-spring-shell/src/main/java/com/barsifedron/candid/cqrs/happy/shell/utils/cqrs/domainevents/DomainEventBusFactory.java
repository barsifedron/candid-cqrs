package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.domainevents;


import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.barsifedron.candid.cqrs.domainevent.MapDomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.ToProcessAfterMainTransaction;
import com.barsifedron.candid.cqrs.spring.DomainEventHandlersRegistry;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.function.Predicate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DomainEventBusFactory {

    private DomainEventHandlersRegistry domainEventHandlersRegistry;

    @Inject
    public DomainEventBusFactory(ApplicationContext applicationContext) {
        this.domainEventHandlersRegistry = new DomainEventHandlersRegistry(
                applicationContext,
                 "com.barsifedron.candid.cqrs.springboot.app",
                "com.barsifedron.candid.cqrs.happy.domainevents");
    }


    /**
     * This bus only include and execute event handlers NOT marked with :
     * @see com.barsifedron.candid.cqrs.spring.domainevent.ToProcessAfterMainTransaction
     */
    public DomainEventBus inMainTransactionDomainEventBus() {
        return new MapDomainEventBus(domainEventHandlersRegistry
                .handlersList()
                .satisfying(toProcessWithinMainTransaction())
                .asMap());
    }

    /**
     * This bus only includes and executes event handlers marked with :
     * @see ToProcessAfterMainTransaction
     */
    public DomainEventBus afterMainTransactionDomainEventBus() {
        return new MapDomainEventBus(domainEventHandlersRegistry
                .handlersList()
                .satisfying(toProcessAfterMainTransaction())
                .asMap());
    }

    /**
     * This bus will include and execute ALL handlers found in the configured classpath
     */
    public DomainEventBus withAllHandlersEventBus() {
        return new MapDomainEventBus(domainEventHandlersRegistry.handlersList().asMap());
    }

    private Predicate<Class<? extends DomainEventHandler>> toProcessAfterMainTransaction() {
        return handlerType -> ToProcessAfterMainTransaction.class.isAssignableFrom(handlerType);
    }

    private Predicate<Class<? extends DomainEventHandler>> toProcessWithinMainTransaction() {
        return toProcessAfterMainTransaction().negate();
    }
}
