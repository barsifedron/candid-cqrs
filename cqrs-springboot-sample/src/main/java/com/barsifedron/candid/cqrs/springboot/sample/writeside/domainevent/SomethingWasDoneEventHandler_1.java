package com.barsifedron.candid.cqrs.springboot.sample.writeside.domainevent;

import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SomethingWasDoneEventHandler_1 implements DomainEventHandler<SomethingWasDoneEvent> {

    private final static Logger LOGGER = Logger.getLogger(SomethingWasDoneEventHandler_1.class.getName());

    @Override
    public void handle(SomethingWasDoneEvent event) {

        LOGGER.info("Received event : " + event.toString());
        LOGGER.info("Will process side effect:");
        LOGGER.info("Sending notification email to : " + event.someoneToNotify);

        // write your notification code yourself, I am tired dude.
    }

    @Override
    public Class<SomethingWasDoneEvent> listenTo() {
        return SomethingWasDoneEvent.class;
    }

}
