package com.barsifedron.candid.app.items.domainevents;

import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemName;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class ItemRenamed implements DomainEvent {

    public final ItemId itemId;
    public final ItemName itemNewName;
    public final LocalDateTime publishingTime;
    public final UUID eventId;

    public ItemRenamed(ItemId itemId, ItemName itemNewName) {
        this.itemId = itemId;
        this.itemNewName = itemNewName;
        this.publishingTime = LocalDateTime.now();
        this.eventId = UUID.randomUUID();
    }
}
