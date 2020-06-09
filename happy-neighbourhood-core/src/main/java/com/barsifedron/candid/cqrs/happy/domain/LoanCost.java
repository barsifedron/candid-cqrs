package com.barsifedron.candid.cqrs.happy.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Builder(toBuilder = true)
@EqualsAndHashCode
public class LoanCost {

    final BigDecimal cost;
    final String trace;

    public LoanCost(BigDecimal cost, String aTrace) {
        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Can't create negative costs");
        }
        if (aTrace == null) {
            throw new IllegalArgumentException("Can't create null trace");
        }
        this.cost = cost;
        this.trace = aTrace;
    }

    public LoanCost add(LoanCost other) {
        if (other.equals(NO_COST())) {
            return this;
        }
        if (this.equals(NO_COST())) {
            return other;
        }
        BigDecimal summ = this.cost.add(other.cost);
        String mergedTrace = this.trace + " \n " + other.trace;
        return new LoanCost(summ, mergedTrace);
    }

    public String trace() {
        return trace;
    }

    static LoanCost NO_COST() {
        return new LoanCost(BigDecimal.ZERO, "");
    }

    public static LoanCost witCostAsTracePrefix(BigDecimal cost, String aTrace) {
        return witCostAsTracePrefix(cost, aTrace, "");
    }

    public static LoanCost witCostAsTracePrefix(BigDecimal cost, String aTrace, String unit) {
        return new LoanCost(cost, cost + unit + " : " + aTrace);
    }

    public LoanCostBuilder but() {
        return this.toBuilder();
    }

}
