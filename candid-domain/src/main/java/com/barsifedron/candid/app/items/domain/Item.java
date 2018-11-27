package com.barsifedron.candid.app.items.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Item {

    private ItemId id;
    private ItemName name;
    private LocalDate since;

    public Item(ItemName name) {
        this(new ItemId(), name, LocalDate.now());
    }

    public Item(ItemId id, ItemName name, LocalDate since) {
        this.id = id;
        this.name = name;
        this.since = since;
    }

    public ItemId id() {
        return id;
    }

    public boolean hasName(ItemName candidateName) {
        return Objects.equals(name, candidateName);
    }

    public void renameAs(String newItemName) {
        name = new ItemName(newItemName);
    }

    public ItemName name() {
        return name;
    }
}
