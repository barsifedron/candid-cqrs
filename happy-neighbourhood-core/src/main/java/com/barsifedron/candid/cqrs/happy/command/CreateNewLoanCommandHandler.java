package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.happy.domain.Item;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.ItemsRepository;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.LoanId;
import com.barsifedron.candid.cqrs.happy.domain.LoanRepository;
import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import com.barsifedron.candid.cqrs.happy.domainevents.NewLoanCreatedDomainEvent;

import javax.inject.Inject;
import java.time.LocalDate;

public class CreateNewLoanCommandHandler implements CommandHandler<LoanId, CreateNewLoanCommand> {

    private final LoanRepository loans;
    private final ItemsRepository items;
    private final MembersRepository members;

    @Inject
    public CreateNewLoanCommandHandler(
            LoanRepository loans,
            ItemsRepository items,
            MembersRepository members) {
        this.loans = loans;
        this.items = items;
        this.members = members;
    }

    @Override
    public CommandResponse<LoanId> handle(CreateNewLoanCommand command) {

        validate(command);

        Item item = items.get(new ItemId(command.itemId));
        Member member = members.get(new MemberId(command.memberId));

        LocalDate borrowedOn = command.borrowedOn != null ? command.borrowedOn : LocalDate.now();
        Loan newLoan = Loan
                .builder()
                .id(new LoanId(command.loanId))
                .itemId(item.id())
                .memberId(member.memberId())
                .borrowedOn(borrowedOn)
                .regularDailyRate(item.dailyRate())
                .dailyFineWhenLate(item.dailyFineWhenLateReturn())
                .expectedReturnOn(borrowedOn.plusDays(item.maximumLoanPeriod()))
                .build();

        loans.add(newLoan);

        NewLoanCreatedDomainEvent domainEvent = NewLoanCreatedDomainEvent
                .builder()
                .borrowedOn(borrowedOn)
                .itemId(item.id().id())
                .itemName(item.name())
                .memberId(member.memberId().id())
                .regularDailyRate(item.dailyRate())
                .dailyFineWhenLate(item.dailyFineWhenLateReturn())
                .expectedReturnOn(borrowedOn.plusDays(item.maximumLoanPeriod()))
                .memberFirstname(member.firstName())
                .memberFirstname(member.surname())
                .email(member.email())
                .build();

        return CommandResponse
                .empty()
                .withResult(newLoan.id())
                .withAddedDomainEvents(domainEvent);
    }

    private void validate(CreateNewLoanCommand command) {

        Member member = members.get(new MemberId(command.memberId));
        if (member == null) {
            throw new MemberDoesNotExistException(command.memberId);
        }

        Item item = items.get(new ItemId(command.itemId));
        if (item == null) {
            throw new ItemDoesNotExistException(command.itemId);
        }

        if (loans.forMember(member.memberId(), Loan.STATUS.IN_PROGRESS).size() > 0) {
            throw new MemberHasLoanInProgressException(member.memberId());
        }

        if (loans.forItem(item.id(), Loan.STATUS.IN_PROGRESS).size() > 0) {
            throw new ItemHasLoanInProgressException(item.id());
        }
    }

    @Override
    public Class<CreateNewLoanCommand> listenTo() {
        return CreateNewLoanCommand.class;
    }

    public static class ItemHasLoanInProgressException extends RuntimeException {
        public ItemHasLoanInProgressException(ItemId itemId) {
            super(String.format("Item %s has already a loan in progress", itemId.id()));
        }
    }

    public static class MemberHasLoanInProgressException extends RuntimeException {
        public MemberHasLoanInProgressException(MemberId memberId) {
            super(String.format("Member %s has already a loan in progress", memberId.id()));
        }
    }

    public static class ItemDoesNotExistException extends RuntimeException {
        public ItemDoesNotExistException(String itemId) {
            super(String.format("The item %s does not exist", itemId));
        }
    }

    public static class MemberDoesNotExistException extends RuntimeException {
        public MemberDoesNotExistException(String memberId) {
            super(String.format("The member %s does not exist", memberId));
        }
    }
}
