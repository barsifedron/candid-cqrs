package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.happy.domain.Item;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.ItemsRepository;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.LoanRepository;
import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import com.barsifedron.candid.cqrs.happy.domainevents.ItemReturnedDomainEvent;

import javax.inject.Inject;
import java.util.List;

public class ReturnItemCommandHandler implements CommandHandler<NoResult, ReturnItemCommand> {

    private ItemsRepository items;
    private LoanRepository loans;
    private MembersRepository members;

    @Inject
    public ReturnItemCommandHandler(ItemsRepository items, LoanRepository loans,
            MembersRepository members) {
        this.items = items;
        this.loans = loans;
        this.members = members;
    }

    @Override
    public CommandResponse<NoResult> handle(ReturnItemCommand command) {

        validate(command);


        List<Loan> activeLoans = this.loans.forItem(new ItemId(command.itemId), Loan.STATUS.IN_PROGRESS);
        if (activeLoans.isEmpty()) {
            return CommandResponse.empty();
        }

        Loan loan = activeLoans.get(0);
        Item item = items.get(new ItemId(command.itemId));
        Member member = members.get(loan.memberId());

        loan.returnItem();

        ItemReturnedDomainEvent domainEvent = ItemReturnedDomainEvent
                .builder()
                .itemId(item.id().id())
                .memberId(loan.memberId().id())
                .borrowedOn(loan.boorowedOn())
                .effectiveReturnOn(loan.effectiveReturnOn())
                .dailyFineWhenLate(loan.dailyFineWhenLate())
                .expectedReturnOn(loan.expectedReturnOn())
                .regularDailyRate(loan.regularDailyRate())
                .email(member.email())
                .memberFirstname(member.firstName())
                .memberSurname(member.surname())
                .itemName(item.name())
                .bill(loan.bill())
                .build();

        return CommandResponse.empty().withAddedDomainEvents(domainEvent);
    }

    private void validate(ReturnItemCommand command) {
        Item iShouldExist = items.get(new ItemId(command.itemId));
        if (iShouldExist == null) {
            throw new UnknownItemException(command.itemId);
        }

        List<Loan> loans = this.loans.forItem(new ItemId(command.itemId), Loan.STATUS.values());

        List<Loan> activeLoan = this.loans.forItem(new ItemId(command.itemId), Loan.STATUS.IN_PROGRESS);
        if (activeLoan.isEmpty() && command.ifNoActiveLoanIsFound == ReturnItemCommand.IF_NO_ACTIVE_LOAN_FOUND.FAIL) {
            throw new NoActiveLoanForItemException(new ItemId(command.itemId));
        }
    }

    @Override
    public Class<ReturnItemCommand> listenTo() {
        return ReturnItemCommand.class;
    }

    public static class UnknownItemException extends RuntimeException {
        public UnknownItemException(String itemId) {
            super(String.format("No item with id %s exists", itemId));
        }
    }

    public class NoActiveLoanForItemException extends RuntimeException {
        public NoActiveLoanForItemException(ItemId itemId) {
            super(String.format("No active loan could be found for item with id %s ", itemId));
        }
    }
}
