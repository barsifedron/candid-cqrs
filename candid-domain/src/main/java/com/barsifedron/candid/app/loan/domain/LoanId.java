package com.barsifedron.candid.app.loan.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class LoanId {

    private final String id;

    public LoanId() {
        this(UUID.randomUUID().toString());
    }

    public LoanId(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LoanId loanId = (LoanId) o;

        return new EqualsBuilder()
                .append(id, loanId.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }
}
