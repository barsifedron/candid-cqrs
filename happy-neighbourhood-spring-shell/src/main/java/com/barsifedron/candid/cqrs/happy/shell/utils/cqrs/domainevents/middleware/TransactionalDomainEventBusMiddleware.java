package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.domainevents.middleware;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class TransactionalDomainEventBusMiddleware implements DomainEventBusMiddleware {


    @Override
    @Transactional
    public void dispatch(DomainEvent event, DomainEventBus next) {
        runInTransaction(event, next);
    }

    /**
     * For some reason, the above @Transactional method is not taken into account by Spring.
     * This might have to do with a clash between spring proxy and the middleware interface.
     * Till I figure it out, calling this method instead from the  bus does the work
     */
    @Transactional
    public void runInTransaction(DomainEvent event, DomainEventBus next) {
        next.dispatch(event);
    }
}
