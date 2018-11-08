package com.barsifedron.candid.app.loan.command;

import com.barsifedron.candid.app.items.domain.Item;
import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemName;
import com.barsifedron.candid.app.items.domain.ItemsRepository;
import com.barsifedron.candid.app.items.infrastructure.InMemoryItemsRepository;
import com.barsifedron.candid.app.loan.domain.Loan;
import com.barsifedron.candid.app.loan.domain.LoanId;
import com.barsifedron.candid.app.loan.domain.LoanRepository;
import com.barsifedron.candid.app.loan.infrastructure.InMemoryLoanRepository;
import com.barsifedron.candid.app.members.domain.Member;
import com.barsifedron.candid.app.members.domain.MemberId;
import com.barsifedron.candid.app.members.domain.MembersRepository;
import com.barsifedron.candid.app.members.infrastructure.InMemoryMembersRepository;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class LendItemToMemberCommandHandlerTest {


    @Test(expected = RuntimeException.class)
    public void shouldFailToBorrowItemWhenMemberDoesNotExist() {

        LoanRepository loanRepository = new InMemoryLoanRepository();
        ItemsRepository itemsRepository = new InMemoryItemsRepository();
        MembersRepository membersRepository = new InMemoryMembersRepository();

        ItemId itemId = new ItemId("itemId");
        itemsRepository.add(new Item(
                itemId,
                new ItemName("Hammer"),
                LocalDate.now()));
        LendItemToMemberCommandHandler lendItemToMemberCommandHandler = new LendItemToMemberCommandHandler(
                loanRepository,
                itemsRepository,
                membersRepository);

        lendItemToMemberCommandHandler.handle(new LendItemToMember(
                itemId.id(),
                new MemberId("nonExistingMemberId").id()));
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailToBorrowWhenItemDoesNotExist() {

        LoanRepository loanRepository = new InMemoryLoanRepository();
        ItemsRepository itemsRepository = new InMemoryItemsRepository();
        MembersRepository membersRepository = new InMemoryMembersRepository();

        MemberId memberId = new MemberId("memberId");
        membersRepository.add(new Member(
                memberId,
                "Buffy",
                "Summers",
                "b.summers@bloodthirst.com"));

        LendItemToMemberCommandHandler lendItemToMemberCommandHandler = new LendItemToMemberCommandHandler(
                loanRepository,
                itemsRepository,
                membersRepository);

        lendItemToMemberCommandHandler.handle(new LendItemToMember(
                new ItemId("nonExistingItemId").id(),
                memberId.id()));

    }

    @Test
    public void shouldBeAbleToBorrowItem() {

        ItemsRepository itemsRepository = new InMemoryItemsRepository();
        ItemId itemId = new ItemId("itemId");
        itemsRepository.add(new Item(
                itemId,
                new ItemName("Hammer"),
                LocalDate.now()));

        MembersRepository membersRepository = new InMemoryMembersRepository();
        MemberId memberId = new MemberId("memberId");
        membersRepository.add(new Member(
                memberId,
                "Buffy",
                "Summers",
                "b.summers@bloodthirst.com"));

        LoanRepository loanRepository = new InMemoryLoanRepository();
        LendItemToMemberCommandHandler lendItemToMemberCommandHandler = new LendItemToMemberCommandHandler(
                loanRepository,
                itemsRepository,
                membersRepository);
        CommandResponse<LoanId> response = lendItemToMemberCommandHandler.handle(new LendItemToMember(
                itemId.id(),
                memberId.id()));

        Loan loan = loanRepository.get(response.result);
        Assert.assertTrue(loanRepository.get(loan.id()).wasBorrowedOn(LocalDate.now()));
        Assert.assertTrue(loanRepository.get(loan.id()).borrowerIs(memberId.id()));
        Assert.assertTrue(loanRepository.get(loan.id()).borrowedItemIs(itemId.id()));
        Assert.assertTrue(loanRepository.get(loan.id()).hasStatus(Loan.STATUS.BORROWED));
    }
}