package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.domainevents.middleware;


import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;

import java.util.logging.Logger;


public class ExecutionTimeLoggingDomainEventsBusMiddleware implements DomainEventBusMiddleware {

    private final Logger LOGGER = Logger.getLogger(ExecutionTimeLoggingDomainEventsBusMiddleware.class.getName());

    @Override
    public void dispatch(DomainEvent event, DomainEventBus bus) {
        long timeBefore = System.nanoTime();
        bus.dispatch(event);
        long timeAfter = System.nanoTime();

        LOGGER.info("" +
                "Done processing event of type : " + event.getClass().getName() +
                "\nEvent execution time was :" + ((timeAfter - timeBefore) / 1000000) + " ms");
    }
}
