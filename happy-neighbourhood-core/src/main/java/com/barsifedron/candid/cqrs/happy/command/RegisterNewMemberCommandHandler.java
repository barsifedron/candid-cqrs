package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import com.barsifedron.candid.cqrs.happy.domainevents.NewMemberRegisteredDomainEvent;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

public class RegisterNewMemberCommandHandler implements CommandHandler<MemberId, RegisterNewMemberCommand> {

    private final MembersRepository members;

    @Inject
    public RegisterNewMemberCommandHandler(MembersRepository members) {
        this.members = members;
    }

    @Override
    @Transactional
    public CommandResponse<MemberId> handle(RegisterNewMemberCommand command) {

        Member shouldNotExist = members.withEmail(command.email);
        if (shouldNotExist != null) {
            throw new EmailAlreadyInUseException(command.email);
        }

        Member shouldAlsoNotExist = members.get(new MemberId(command.memberId));
        if (shouldAlsoNotExist != null) {
            throw new MemberIdAlreadyInUseException(command.memberId);
        }

        Member newMember = Member
                .builder()
                .email(command.email)
                .surname(command.surname)
                .firstname(command.firstname)
                .registeredOn(LocalDateTime.now())
                .memberId(new MemberId(command.memberId))
                .build();

        members.add(newMember);

        NewMemberRegisteredDomainEvent domainEvent = NewMemberRegisteredDomainEvent
                .builder()
                .email(command.email)
                .surname(command.surname)
                .memberId(command.memberId)
                .firstname(command.firstname)
                .registeredOn(newMember.registeredOn())
                .build();

        return CommandResponse
                .empty()
                .withResult(newMember.memberId())
                .withAddedDomainEvents(domainEvent);
    }

    @Override
    public Class<RegisterNewMemberCommand> listenTo() {
        return RegisterNewMemberCommand.class;
    }

    public static class EmailAlreadyInUseException extends RuntimeException {
        public EmailAlreadyInUseException(String email) {
            super(String.format("The email %s is already assigned to another user.", email));
        }
    }

    public static class MemberIdAlreadyInUseException extends RuntimeException {
        public MemberIdAlreadyInUseException(String memberId) {
            super(String.format("The member id %s is already assigned to another user.", memberId));
        }
    }
}
