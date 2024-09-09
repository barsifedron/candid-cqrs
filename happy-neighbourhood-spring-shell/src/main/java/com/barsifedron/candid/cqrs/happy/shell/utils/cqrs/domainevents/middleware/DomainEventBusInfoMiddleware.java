package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.domainevents.middleware;

import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.domainevents.DomainEventToLog;

import java.util.logging.Logger;

public class DomainEventBusInfoMiddleware implements DomainEventBusMiddleware {

    private String infoMessage;
    private final Logger LOGGER = java.util.logging.Logger.getLogger(DomainEventBusInfoMiddleware.class.getName());

    public DomainEventBusInfoMiddleware(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    @Override
    public void dispatch(DomainEvent domainEvent, DomainEventBus domainEventBus) {

        LOGGER.info(infoMessage);

        LOGGER.info("Processing event of type " + domainEvent.getClass().getName());
        if (DomainEventToLog.class.isAssignableFrom(domainEvent.getClass())) {
            LOGGER.info("Details of event : " + domainEvent.toString());
        }
        domainEventBus.dispatch(domainEvent);
        LOGGER.info("Domain event has been processed.");
    }

}
