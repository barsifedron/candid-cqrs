package com.barsifedron.candid.cqrs.happy.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanCostTest {

    @Test
    void name() {

        Loan loan = Loan
                .builder()
                .borrowedOn(LocalDate.now().minusDays(600))
                .regularDailyRate(new BigDecimal("0.0"))
                .dailyFineWhenLate(new BigDecimal("0.5"))
                .expectedReturnOn(LocalDate.now().minusDays(598))
                .effectiveReturnOn(LocalDate.now().minusDays(596))

                .build();

        LoanCost loanCost = loan.loanCost();

        System.out.println("total cost = " + loanCost.cost);
        System.out.println("trace = " + loanCost.trace());

    }
}