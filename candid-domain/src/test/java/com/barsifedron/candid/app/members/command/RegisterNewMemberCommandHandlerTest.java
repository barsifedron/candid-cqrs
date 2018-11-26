package com.barsifedron.candid.app.members.command;

import com.barsifedron.candid.app.members.domain.Member;
import com.barsifedron.candid.app.members.domain.MemberId;
import com.barsifedron.candid.app.members.domain.MembersRepository;
import com.barsifedron.candid.app.members.domainevents.NewMemberRegistered;
import com.barsifedron.candid.app.members.infrastructure.InMemoryMembersRepository;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import org.junit.Assert;
import org.junit.Test;


public class RegisterNewMemberCommandHandlerTest {


    @Test(expected = RegisterNewMemberCommandHandler.DuplicateMemberEmailException.class)
    public void shouldFailToRegisterMemberInCaseOfDuplicateEmail() {

        MembersRepository membersRepository = new InMemoryMembersRepository();
        RegisterNewMemberCommandHandler handler = new RegisterNewMemberCommandHandler(membersRepository);

        handler.handle(new RegisterNewMember(
                "jack@hotmail.com",
                "Jack",
                "Malone"));

        handler.handle(new RegisterNewMember(
                "jack@hotmail.com",
                "Jack",
                "Malone"));
    }

    @Test
    public void shouldRegisterMemberIfDuplicateNameButDifferentEmail() {

        MembersRepository membersRepository = new InMemoryMembersRepository();
        RegisterNewMemberCommandHandler handler = new RegisterNewMemberCommandHandler(membersRepository);

        handler.handle(new RegisterNewMember(
                "jack@hotmail.com",
                "Jack",
                "Malone"));

        handler.handle(new RegisterNewMember(
                "otherjack@hotmail.com",
                "Jack",
                "Malone"));
    }

    @Test
    public void shouldReturnDomainEventAfterNewRegistration() {

        MembersRepository membersRepository = new InMemoryMembersRepository();
        RegisterNewMemberCommandHandler handler = new RegisterNewMemberCommandHandler(membersRepository);

        CommandResponse<MemberId> response = handler.handle(new RegisterNewMember(
                "jack@hotmail.com",
                "Jack",
                "Malone"));

        Assert.assertEquals(1, response.domainEvents.size());
        Assert.assertEquals(NewMemberRegistered.class, response.domainEvents.get(0).getClass());
    }

    @Test
    public void shouldReturnProperlyConstructedMember() {

        MembersRepository membersRepository = new InMemoryMembersRepository();
        RegisterNewMemberCommandHandler handler = new RegisterNewMemberCommandHandler(membersRepository);

        CommandResponse<MemberId> response = handler.handle(new RegisterNewMember(
                "jack@hotmail.com",
                "Jack",
                "Malone"));

        Member member = membersRepository.get(response.result);
        Assert.assertTrue(member.hasEmail("jack@hotmail.com"));
        Assert.assertTrue(member.hasFirstName("Jack"));
        Assert.assertTrue(member.hasFamilyName("Malone"));

    }
}