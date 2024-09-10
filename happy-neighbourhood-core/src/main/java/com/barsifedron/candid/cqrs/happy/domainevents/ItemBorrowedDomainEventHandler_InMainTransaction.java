package com.barsifedron.candid.cqrs.happy.domainevents;

import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.barsifedron.candid.cqrs.domainevent.ToProcessAfterMainTransaction;
import com.barsifedron.candid.cqrs.happy.command.BorrowItemCommandHandler;
import com.barsifedron.candid.cqrs.happy.domain.Email;
import com.barsifedron.candid.cqrs.happy.domain.EmailRepository;

import javax.inject.Inject;
import java.util.logging.Logger;

public class ItemBorrowedDomainEventHandler_InMainTransaction implements DomainEventHandler<ItemBorrowedDomainEvent> {

    static Logger LOGGER = Logger.getLogger(ItemBorrowedDomainEventHandler_InMainTransaction.class.getName());

    @Inject
    public ItemBorrowedDomainEventHandler_InMainTransaction() {
    }

    @Override
    public void handle(ItemBorrowedDomainEvent event) {

        LOGGER.info("" +
                "This event handler executes WITHIN the main transaction" +
                "as it makes NO use of the marker interface : ToProcessAfterMainTransaction");

        // add your code that needs to be processed in the main transaction here.

        // ...
       // ...


    }

    @Override
    public Class<ItemBorrowedDomainEvent> listenTo() {
        return ItemBorrowedDomainEvent.class;
    }
}
