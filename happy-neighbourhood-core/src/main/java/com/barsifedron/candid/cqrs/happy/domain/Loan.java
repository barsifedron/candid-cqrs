package com.barsifedron.candid.cqrs.happy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Access(AccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Entity
@Table(name = "loan")
public class Loan {

    @Embedded
    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(updatable = false))
    private LoanId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(insertable = false, updatable = false))
    private ItemId itemId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(insertable = false, updatable = false))
    private MemberId memberId;

    @Column(name = "borrowedon")
    private LocalDate borrowedOn;

    @Column(name = "expectedreturnon")
    private LocalDate expectedReturnOn;

    @Column(name = "effectivereturnon")
    private LocalDate effectiveReturnOn;

    @Column(name = "regulardailyrate")
    private BigDecimal regularDailyRate;

    @Column(name = "dailyfinewhenlate")
    private BigDecimal dailyFineWhenLate;

    @Column(name = "status")
    private STATUS status;

    public boolean hasItemId(ItemId candidate) {
        return Objects.equals(itemId, candidate);
    }

    public boolean hasStatus(STATUS candidate) {
        return status == candidate;
    }

    public LoanId id() {
        return id;
    }

    public boolean hasMemberId(MemberId candidate) {
        return Objects.equals(memberId, candidate);
    }

    public boolean wasBorrowedOn(LocalDate candidate) {
        return borrowedOn.equals(candidate);
    }

    public enum STATUS {
        IN_PROGRESS, RETURNED, MANUALLY_CLOSED
    }

    public LoanCost loanCost() {

        if (effectiveReturnOn == null && LocalDate.now().isBefore(borrowedOn)) {
            throw new RuntimeException("Can not calculate loan cost in the current state");
        }

        return IntStream
                .range(0, 100000)
                .mapToObj(n -> borrowedOn.plusDays(n))
                .filter(day -> !day.isAfter(effectiveReturnOn))
                .map(day -> {

                    List<LoanCost> dailyCosts = new ArrayList<>();
                    dailyCosts.add(new LoanCost(
                            regularDailyRate,
                            String.format(
                                    "%s : %s € - regular daily fee",
                                    day.toString(),
                                    regularDailyRate.toString())));

                    if (day.isAfter(expectedReturnOn)) {
                        dailyCosts.add(new LoanCost(
                                dailyFineWhenLate,
                                String.format(
                                        "%s : %s € - fine for late return",
                                        day.toString(),
                                        dailyFineWhenLate.toString())));
                    }

                    return dailyCosts;
                })
                .flatMap(Collection::stream)
                .reduce(LoanCost.NO_COST(), LoanCost::add);

    }

}
