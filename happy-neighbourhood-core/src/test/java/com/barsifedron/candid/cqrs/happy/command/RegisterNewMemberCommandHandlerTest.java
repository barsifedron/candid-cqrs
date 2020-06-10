package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import com.barsifedron.candid.cqrs.happy.domainevents.NewMemberRegisteredDomainEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegisterNewMemberCommandHandlerTest {

    @Test
    void shouldRefuseCreationIfEmailIsAlreadyTaken() {

        MembersRepository.InMemory members = new MembersRepository.InMemory();
        members.add(Member
                .builder()
                .email("iAlready@exist.com")
                .build());

        RegisterNewMemberCommand command = RegisterNewMemberCommand
                .builder()
                .email("iAlready@exist.com")
                .build();

        RegisterNewMemberCommandHandler handler = new RegisterNewMemberCommandHandler(members);

        assertThrows(
                RegisterNewMemberCommandHandler.EmailAlreadyInUseException.class,
                () -> handler.handle(command));

    }

    @Test
    void shouldRefuseCreationIfMemberIdIsAlreadyTaken() {

        MembersRepository.InMemory members = new MembersRepository.InMemory();
        members.add(Member
                .builder()
                .memberId(new MemberId("this id exists in the db"))
                .email("iAlready@exist.com")
                .build());

        RegisterNewMemberCommand command = RegisterNewMemberCommand
                .builder()
                .memberId("this id exists in the db")
                .email("iDoNotAlready@exist.com")
                .build();

        RegisterNewMemberCommandHandler handler = new RegisterNewMemberCommandHandler(members);

        assertThrows(
                RegisterNewMemberCommandHandler.MemberIdAlreadyInUseException.class,
                () -> handler.handle(command));

    }

    @Test
    void shouldRegisterAMemberWithAllTheCorrectData() {

        MembersRepository.InMemory members = new MembersRepository.InMemory();
        members.add(Member
                .builder()
                .memberId(new MemberId("member-john-cena"))
                .firstname("john")
                .surname("cena")
                .email("john@cena.com")
                .registeredOn(LocalDateTime.now().minusDays(10))
                .build());

        RegisterNewMemberCommand command = RegisterNewMemberCommand
                .builder()
                .firstname("Daniel")
                .surname("Balavoine")
                .email("d.balavoine@laziza.com")
                .memberId("XXX-987-09")
                .build();

        RegisterNewMemberCommandHandler handler = new RegisterNewMemberCommandHandler(members);

        CommandResponse<MemberId> commandResponse = assertDoesNotThrow(() -> handler.handle(command));

        Member newMember = members.get(new MemberId("XXX-987-09"));
        assertTrue(newMember.hasEmail("d.balavoine@laziza.com"));
        assertTrue(newMember.hasFirstname("Daniel"));
        assertTrue(newMember.hasSurname("Balavoine"));

        assertEquals(1, commandResponse.domainEvents.size(), "Should have generated one domain event");
        assertTrue(
                NewMemberRegisteredDomainEvent.class.isAssignableFrom(commandResponse.domainEvents.get(0).getClass()),
                "Domain event should be of correct type");

    }
}