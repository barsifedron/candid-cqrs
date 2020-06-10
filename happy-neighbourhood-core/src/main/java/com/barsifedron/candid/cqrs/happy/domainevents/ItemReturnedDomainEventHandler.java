package com.barsifedron.candid.cqrs.happy.domainevents;

import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.barsifedron.candid.cqrs.happy.command.BorrowItemCommandHandler;
import com.barsifedron.candid.cqrs.happy.domain.Email;
import com.barsifedron.candid.cqrs.happy.domain.EmailRepository;

import javax.inject.Inject;

public class ItemReturnedDomainEventHandler implements DomainEventHandler<ItemReturnedDomainEvent> {

    private final EmailRepository emailRepository;

    @Inject
    public ItemReturnedDomainEventHandler(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public void handle(ItemReturnedDomainEvent event) {

        Email email = Email
                .builder()
                .email(event.email)
                .body(String.format(
                        ""
                                + "Hi %s.\n"
                                + "You borrowed the item : %s from the %s to the %s.\n"
                                + "Here is the detail of what you will be charged.\n"
                                + "Kind regards.\n"
                                + "The happy neighbourhood community.\\n"
                                + "%s",
                        event.memberFirstname,
                        event.itemName,
                        event.borrowedOn.toString(),
                        event.effectiveReturnOn.toString(),
                        event.bill))
                .status(Email.EMAIL_STATUS.TO_BE_SENT)
                .build();


        /**
         * We store the email to be sent. Another process will manage retrieval and actually sending of these
         */
        emailRepository.add(email);

    }

    @Override
    public Class<ItemReturnedDomainEvent> listenTo() {
        return ItemReturnedDomainEvent.class;
    }
}
