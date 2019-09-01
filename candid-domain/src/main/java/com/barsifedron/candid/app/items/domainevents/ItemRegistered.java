package com.barsifedron.candid.app.items.domainevents;

import com.barsifedron.candid.app.items.domain.Item;
import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemName;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class ItemRegistered implements DomainEvent {

    public final ItemId itemId;
    public final ItemName name;
    public final LocalDateTime publishingTime;
    public final UUID eventId;
    private final String aggregateType;

    public ItemRegistered(ItemId itemId, ItemName name) {
        this.itemId = itemId;
        this.name = name;
        this.publishingTime = LocalDateTime.now();
        this.eventId = UUID.randomUUID();
        this.aggregateType = Item.class.getName();
    }
}
