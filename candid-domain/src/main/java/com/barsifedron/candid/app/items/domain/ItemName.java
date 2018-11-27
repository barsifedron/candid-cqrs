package com.barsifedron.candid.app.items.domain;

import java.util.Objects;

public class ItemName {

    private String name;

    public ItemName(String name) {
        this.name = name;
    }

    public String asString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemName itemName = (ItemName) o;
        return Objects.equals(name, itemName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
