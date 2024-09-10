package com.barsifedron.candid.cqrs.happy.domainevents;

import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.barsifedron.candid.cqrs.happy.domain.Email;
import com.barsifedron.candid.cqrs.happy.domain.EmailRepository;

import javax.inject.Inject;

public class NewMemberRegisteredDomainEventHandler implements DomainEventHandler<NewMemberRegisteredDomainEvent> {

    private final EmailRepository emailRepository;

    @Inject
    public NewMemberRegisteredDomainEventHandler(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public void handle(NewMemberRegisteredDomainEvent event) {

        Email email = Email
                .builder()
                .email(event.email)
                .body(String.format(
                        ""
                                + "Hi %s.\n"
                                + "You are now a member of the happy neighborhood community.\n"
                                + "Your member id is %s.\n"
                                + "We are glad to have you in our team!.\\n",
                        event.firstname,
                        event.memberId))
                .status(Email.EMAIL_STATUS.TO_BE_SENT)
                .build();


        /**
         * We store the email to be sent. Another process will manage retrieval and actually sending of these
         */
        emailRepository.add(email);

    }


    @Override
    public Class<NewMemberRegisteredDomainEvent> listenTo() {
        return NewMemberRegisteredDomainEvent.class;
    }
}
