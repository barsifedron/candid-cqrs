package com.barsifedron.candid.app.loan.domain;

import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.members.domain.MemberId;

import java.time.LocalDate;
import java.util.Objects;

public class Loan {

    private LoanId id;
    private ItemId itemId;
    private MemberId memberId;
    private LocalDate borrowedAt;
    private LocalDate toReturnBefore;
    private LocalDate returnedAt;
    private STATUS loanStatus;

    public Loan(ItemId itemId, MemberId memberId) {
        this(
                new LoanId(),
                itemId,
                memberId,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                STATUS.BORROWED
        );
    }

    public Loan(LoanId id, ItemId itemId, MemberId memberId, LocalDate borrowedAt, LocalDate toReturnBefore, STATUS loanStatus) {
        this.id = id;
        this.itemId = itemId;
        this.memberId = memberId;
        this.borrowedAt = borrowedAt;
        this.toReturnBefore = toReturnBefore;
        this.loanStatus = loanStatus;
    }

    public LoanId id() {
        return id;
    }

    public boolean borrowedItemIs(String candidateItemId) {
        return itemId.equals(new ItemId(candidateItemId));
    }

    public boolean borrowerIs(String candidateMemberId) {
        return Objects.equals(memberId, new MemberId(candidateMemberId));
    }

    public boolean wasBorrowedOn(LocalDate candidateDate) {
        return candidateDate != null && borrowedAt.isEqual(candidateDate);
    }

    public boolean hasStatus(STATUS candidate) {
        return loanStatus == candidate;
    }

    public void markReturned() {
        this.returnedAt = LocalDate.now();
        this.loanStatus = STATUS.RETURNED;
    }

    public boolean hasItemId(ItemId candidate) {
        return this.itemId.equals(candidate);
    }

    public enum STATUS {
        BORROWED, RETURNED, MANUALLY_CLOSED
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", memberId=" + memberId +
                ", borrowedAt=" + borrowedAt +
                ", toReturnBefore=" + toReturnBefore +
                ", loanStatus=" + loanStatus +
                '}';
    }
}
