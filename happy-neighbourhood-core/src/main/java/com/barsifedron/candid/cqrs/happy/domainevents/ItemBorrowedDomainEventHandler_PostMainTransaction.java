package com.barsifedron.candid.cqrs.happy.domainevents;

import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.barsifedron.candid.cqrs.domainevent.ToProcessAfterMainTransaction;
import com.barsifedron.candid.cqrs.happy.command.BorrowItemCommandHandler;
import com.barsifedron.candid.cqrs.happy.domain.Email;
import com.barsifedron.candid.cqrs.happy.domain.EmailRepository;

import javax.inject.Inject;
import java.util.logging.Logger;

public class ItemBorrowedDomainEventHandler_PostMainTransaction implements DomainEventHandler<ItemBorrowedDomainEvent>, ToProcessAfterMainTransaction {

    private final EmailRepository emailRepository;
    static Logger LOGGER = Logger.getLogger(ItemBorrowedDomainEventHandler_PostMainTransaction.class.getName());


    @Inject
    public ItemBorrowedDomainEventHandler_PostMainTransaction(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public void handle(ItemBorrowedDomainEvent event) {

        LOGGER.info("" +
                "This event handler executes AFTER the main transaction " +
                "due to the use of the marker interface : ToProcessAfterMainTransaction " +
                "AND the proper configuration of the smart command bus. (@see the CommandBusFactory class)");

        if (event.notification == BorrowItemCommandHandler.NOTIFICATION.NONE) {
            return;
        }

        Email email = Email
                .builder()
                .email(event.email)
                .body(String.format(
                        ""
                                + "Hi %s.\n"
                                + "You borrowed the item : %s on the %s.\n"
                                + "This item will cost %s per day and should be returned by the %s."
                                + "After this date, and ADDITIONAL fine of %s per day will be charged on top of the daily rate.\n"
                                + "Kind regards.\n"
                                + "The happy neighbourhood community.",
                        event.memberFirstname,
                        event.itemName,
                        event.borrowedOn.toString(),
                        event.regularDailyRate.toString(),
                        event.expectedReturnOn.toString(),
                        event.dailyFineWhenLate.toString()))
                .status(Email.EMAIL_STATUS.TO_BE_SENT)
                .build();

        /**
         * We store the email to be sent. Another process will manage retrieval and actually sending of these
         */
        emailRepository.add(email);
    }

    @Override
    public Class<ItemBorrowedDomainEvent> listenTo() {
        return ItemBorrowedDomainEvent.class;
    }
}
