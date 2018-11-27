package com.barsifedron.candid.app.members.command;


import com.barsifedron.candid.app.members.domain.Member;
import com.barsifedron.candid.app.members.domain.MemberId;
import com.barsifedron.candid.app.members.domain.MembersRepository;
import com.barsifedron.candid.app.members.domainevents.NewMemberRegistered;
import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import javax.inject.Inject;

/**
 * Deals with the registering of a new member. I do not go crazy on domain validation here to keep things simple.
 */
public class RegisterNewMemberCommandHandler implements CommandHandler<MemberId, RegisterNewMember> {

    private final MembersRepository membersRepository;

    @Inject
    public RegisterNewMemberCommandHandler(MembersRepository membersRepository) {
        this.membersRepository = membersRepository;
    }

    @Override
    public CommandResponse<MemberId> handle(RegisterNewMember command) {

        // /!\ This is a query! Inside our command/write side
        // While this would be fine in a classic application, when doing CQRS this should
        // at the very least raise your eyebrows.
        //
        // Alternative : This check could be calling a projection table.
        // Other options are here : http://www.cqrs.nu/Faq/command-handlers
        if (membersRepository.hasMemberWithEmail(command.email.toLowerCase())) {
            throw new DuplicateMemberEmailException("A member is already registered with the email :" + command.email);
        }

        // Register member
        Member member = new Member(
                command.email.toLowerCase(),
                command.firstName,
                command.familyName);

        // Simply add to your collections-like repository.
        // You should never deal with transactions at this level.
        // That is the job of the command bus middleware or, at the really worst, of the underlying repository implementation.
        // So no "update", "persist" etc...
        membersRepository.add(member);

        // This is a LOCAL domain event, which can be listened to in order to create "side effects".
        // Not something to send to kafka, aws or others micro services.
        //
        // Send a welcome email, create an activity log, update a counter... none of these things should happen in your command handler.
        // Side effects will only ever be triggered through local domain events.
        // Good news : you have nothing to do but to add the domain events to the returned response. The command bus automatically dispatches them to their rightful handlers.
        // Note : we can generate more than one domain event. But also no event at all.
        DomainEvent domainEvent = new NewMemberRegistered(
                member.memberId(),
                member.email(),
                member.firstName(),
                member.familyName());
        return new CommandResponse<>(member.memberId(), domainEvent);
    }

    @Override
    public Class listenTo() {
        return RegisterNewMember.class;
    }

    static class DuplicateMemberEmailException extends RuntimeException {
        public DuplicateMemberEmailException(String message) {
            super(message);
        }
    }
}
