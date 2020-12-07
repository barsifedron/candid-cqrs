package com.barsifedron.candid.cqrs.happy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

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
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;

@Access(AccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
@ToString
@Entity
@Table(name = "loan")
public class Loan {

    @Embedded
    @EmbeddedId
    @AttributeOverride(name = "loanId", column = @Column(updatable = false))
    private LoanId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "itemid"))
    private ItemId itemId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "memberid"))
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

    @Column(name = "totalcost")
    private BigDecimal totalCost;

    @Column(name = "addedfees")
    private BigDecimal addedFees;

    @Column(name = "detailedcosts")
    @Type(type = "text")
    private String detailedCosts;

    public boolean hasItemId(ItemId candidate) {
        return Objects.equals(itemId, candidate);
    }

    public boolean hasStatusIn(STATUS... candidates) {
        return Stream.of(candidates).anyMatch(candidate -> Objects.equals(status, candidate));
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

    public void returnItem() {

        effectiveReturnOn = LocalDate.now();
        status = STATUS.RETURNED;
        totalCost = fullLoanCostDetailed().cost;
        addedFees = loanCostPenaltiesPart().cost;
        detailedCosts = fullLoanCostDetailed().trace;
    }

    public MemberId memberId() {
        return memberId;
    }

    public LocalDate boorowedOn() {
        return borrowedOn;
    }

    public LocalDate effectiveReturnOn() {
        return effectiveReturnOn;
    }

    public BigDecimal dailyFineWhenLate() {
        return dailyFineWhenLate;
    }

    public LocalDate expectedReturnOn() {
        return effectiveReturnOn;
    }

    public BigDecimal regularDailyRate() {
        return regularDailyRate;
    }

    public String bill() {
        return detailedCosts;
    }

    public enum STATUS {
        IN_PROGRESS, RETURNED, MANUALLY_CLOSED
    }

    public LoanCost loanCostPenaltiesPart() {

        if (effectiveReturnOn == null) {
            throw new RuntimeException("Can not calculate loan cost in the current state");
        }

        return IntStream
                .range(1, 1000)
                .mapToObj(n -> expectedReturnOn.plusDays(n))
                .filter(day -> !day.isAfter(effectiveReturnOn))
                .map(this::dailyFine)
                .reduce(LoanCost.NO_COST(), LoanCost::add);
    }

    public LoanCost loanCostRegularPart() {

        if (effectiveReturnOn == null) {
            throw new RuntimeException("Can not calculate loan cost in the current state");
        }

        return IntStream
                .range(0, 1000)
                .mapToObj(n -> borrowedOn.plusDays(n))
                .filter(day -> !day.isAfter(effectiveReturnOn))
                .map(this::regularFee)
                .reduce(LoanCost.NO_COST(), LoanCost::add);

    }

    public LoanCost fullLoanCostShort() {

        if (effectiveReturnOn == null) {
            throw new RuntimeException("Can not calculate loan cost in the current state");
        }

        LoanCost regularFees = loanCostRegularPart().toBuilder().trace(" € - regular fees ").build();
        LoanCost penalties = loanCostPenaltiesPart().toBuilder().trace(" € - penalties ").build();

        return regularFees.add(penalties);

    }

    public LoanCost fullLoanCostDetailed() {

        if (effectiveReturnOn == null) {
            throw new RuntimeException("Can not calculate loan cost in the current state");
        }

        return IntStream
                .range(0, 1000)
                .mapToObj(n -> borrowedOn.plusDays(n))
                .filter(day -> !day.isAfter(effectiveReturnOn))
                .map(day -> regularFee(day).add(dailyFine(day)))
                .reduce(LoanCost.NO_COST(), LoanCost::add);

    }

    private LoanCost regularFee(LocalDate day) {
        return new LoanCost(
                regularDailyRate,
                String.format(
                        "%s : %s € - regular daily fee",
                        day.toString(),
                        regularDailyRate.toString()));
    }

    private LoanCost dailyFine(LocalDate day) {
        if (day.isAfter(expectedReturnOn)) {
            return new LoanCost(
                    dailyFineWhenLate,
                    format(
                            "%s : %s € - fine for late return",
                            day.toString(),
                            dailyFineWhenLate.toString()));
        }
        return LoanCost.NO_COST();
    }

}
