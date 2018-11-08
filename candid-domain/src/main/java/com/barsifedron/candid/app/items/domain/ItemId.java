package com.barsifedron.candid.app.items.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class ItemId {

    private final String id;

    public ItemId() {
        this(UUID.randomUUID().toString());
    }

    public ItemId(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ItemId itemId = (ItemId) o;

        return new EqualsBuilder()
                .append(id, itemId.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ItemId{" +
                "id='" + id + '\'' +
                '}';
    }
}
