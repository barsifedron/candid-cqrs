package com.barsifedron.candid.app.loan.domainevents;

import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemName;
import com.barsifedron.candid.app.members.domain.MemberId;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class ItemLentToMember implements DomainEvent {

    public ItemId id;
    public ItemName name;
    public MemberId memberId;
    public String firstName;
    public String familyName;

    public UUID eventId;
    public LocalDateTime publishingTime;

    public ItemLentToMember(ItemId id, ItemName name, MemberId memberId, String firstName, String familyName) {

        this.id = id;
        this.name = name;
        this.memberId = memberId;
        this.firstName = firstName;
        this.familyName = familyName;

        this.eventId = UUID.randomUUID();
        this.publishingTime = LocalDateTime.now();
    }
}
