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
import com.barsifedron.candid.cqrs.happy.domainevents.ItemBorrowedDomainEvent;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandResultToLog;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandToLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class BorrowItem implements CommandHandler<LoanId, BorrowItem.Command> {

    private final LoanRepository loans;
    private final ItemsRepository items;
    private final MembersRepository members;

    @Inject
    public BorrowItem(
            LoanRepository loans,
            ItemsRepository items,
            MembersRepository members) {
        this.loans = loans;
        this.items = items;
        this.members = members;
    }

    @Override
    public CommandResponse<LoanId> handle(Command command) {

        validate(command);

        Item item = items.get(new ItemId(command.itemId));
        Member member = members.get(new MemberId(command.memberId));


        Loan newLoan = Loan
                .builder()
                .id(new LoanId(command.loanId))
                .itemId(item.id())
                .memberId(member.memberId())
                .borrowedOn(command.borrowedOn)
                .regularDailyRate(item.dailyRate())
                .dailyFineWhenLate(item.dailyFineWhenLateReturn())
                .expectedReturnOn(command.borrowedOn.plusDays(item.maximumLoanPeriod()))
                .status(Loan.STATUS.IN_PROGRESS)
                .build();

        loans.add(newLoan);

        ItemBorrowedDomainEvent domainEvent = ItemBorrowedDomainEvent
                .builder()
                .borrowedOn(command.borrowedOn)
                .itemId(item.id().id())
                .itemName(item.name())
                .memberId(member.memberId().id())
                .regularDailyRate(item.dailyRate())
                .dailyFineWhenLate(item.dailyFineWhenLateReturn())
                .expectedReturnOn(command.borrowedOn.plusDays(item.maximumLoanPeriod()))
                .memberFirstname(member.firstName())
                .memberFirstname(member.surname())
                //                                        .notification(command.notification)
                .email(member.email())
                .build();

        return CommandResponse
                .empty()
                .withResult(newLoan.id())
                .withAddedDomainEvents(domainEvent);
    }

    private void validate(Command command) {

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
    public Class<Command> listenTo() {
        return Command.class;
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

    public static enum NOTIFICATION {
        NONE, EMAIL_CONFIRMATION_TO_MEMBER
    }

    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class Command
            implements com.barsifedron.candid.cqrs.command.Command<LoanId>, CommandToLog, CommandResultToLog {
        @NotEmpty
        public String loanId;
        @NotEmpty
        public String memberId;
        @NotEmpty
        public String itemId;
        @NotNull
        public LocalDate borrowedOn;
        //        @NotNull
        public NOTIFICATION notification;
    }

}
