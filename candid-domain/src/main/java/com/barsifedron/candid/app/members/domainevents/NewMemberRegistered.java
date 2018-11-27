package com.barsifedron.candid.app.members.domainevents;

import com.barsifedron.candid.app.members.domain.MemberId;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class NewMemberRegistered implements DomainEvent {

    public MemberId memberId;
    public String email;
    public String firstName;
    public String familyName;

    public UUID eventId;
    public LocalDateTime publishingTime;

    public NewMemberRegistered(MemberId memberId, String email, String firstName, String familyName) {

        this.memberId = memberId;
        this.email = email;
        this.firstName = firstName;
        this.familyName = familyName;

        this.publishingTime = LocalDateTime.now();
        this.eventId = UUID.randomUUID();
    }
}
