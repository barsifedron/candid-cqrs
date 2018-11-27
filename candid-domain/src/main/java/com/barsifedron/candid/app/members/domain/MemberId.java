package com.barsifedron.candid.app.members.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class MemberId {

    private final String id;

    public MemberId() {
        this(UUID.randomUUID().toString());
    }

    public MemberId(String uuid) {
        this.id = uuid;
    }

    public String id() {
        return this.id;
    }

    @Override
    public String toString() {
        return "MemberId{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MemberId memberId = (MemberId) o;

        return new EqualsBuilder()
                .append(id, memberId.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }
}
