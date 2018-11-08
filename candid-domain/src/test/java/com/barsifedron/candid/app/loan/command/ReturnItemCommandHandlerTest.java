package com.barsifedron.candid.app.loan.command;

import com.barsifedron.candid.app.items.domain.Item;
import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemName;
import com.barsifedron.candid.app.items.domain.ItemsRepository;
import com.barsifedron.candid.app.items.infrastructure.InMemoryItemsRepository;
import com.barsifedron.candid.app.loan.domain.Loan;
import com.barsifedron.candid.app.loan.domain.LoanRepository;
import com.barsifedron.candid.app.loan.infrastructure.InMemoryLoanRepository;
import com.barsifedron.candid.app.members.domain.Member;
import com.barsifedron.candid.app.members.domain.MemberId;
import com.barsifedron.candid.app.members.domain.MembersRepository;
import com.barsifedron.candid.app.members.infrastructure.InMemoryMembersRepository;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;


public class ReturnItemCommandHandlerTest {

    @Test
    public void shouldReturnItem() {

        // given a loan
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
        Loan loan = new Loan(
                new ItemId("itemId"),
                new MemberId("memberId"));
        loanRepository.add(loan);

        // when you return the item
        ReturnItemCommandHandler returnItemCommandHandler = new ReturnItemCommandHandler(loanRepository);
        returnItemCommandHandler.handle(new ReturnItem(itemId.id()));
        Assert.assertTrue(loanRepository.get(loan.id()).hasStatus(Loan.STATUS.RETURNED));

    }
}