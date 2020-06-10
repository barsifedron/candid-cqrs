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
import java.util.Objects;
import java.util.stream.Stream;

@Access(AccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Entity
@Table(name = "item")
public class Item {

    @Embedded
    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(updatable = false))
    private ItemId id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private LocalDate since;

    @Column(name = "maximumloanperiod")
    private Integer maximumLoanPeriod; // in days

    @Column(name = "regulardailyrate")
    private BigDecimal dailyRate;

    @Column(name = "dailyfinewhenlatereturn")
    private BigDecimal dailyFineWhenLateReturn;

    public ItemId id() {
        return id;
    }

    public BigDecimal dailyRate() {
        return dailyRate;
    }

    public BigDecimal dailyFineWhenLateReturn() {
        return dailyFineWhenLateReturn;
    }

    public Integer maximumLoanPeriod() {
        return maximumLoanPeriod;
    }

    public boolean hasId(ItemId candidate) {
        return id.equals(candidate);
    }

    public boolean hasMaximumLoanPeriod(Integer candidate) {
        return maximumLoanPeriod.equals(candidate);
    }

    public boolean hasName(String candidate) {
        return name.equals(candidate);
    }

    public boolean hasDailyRate(BigDecimal candidate) {
        return candidate != null && dailyRate.compareTo(candidate) == 0;
    }

    public boolean hasDailyFineWhenLateReturn(BigDecimal candidate) {
        return candidate != null && dailyFineWhenLateReturn.compareTo(candidate) == 0;
    }

    public String name() {
        return name;
    }
}
