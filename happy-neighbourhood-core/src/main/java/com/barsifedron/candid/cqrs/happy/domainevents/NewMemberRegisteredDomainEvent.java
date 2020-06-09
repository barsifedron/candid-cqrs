package com.barsifedron.candid.cqrs.happy.domainevents;

import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class NewMemberRegisteredDomainEvent implements DomainEvent {

    public final String email;
    public final String firstname;
    public final String surname;
    public final String memberId;
    public final LocalDateTime registeredOn;

}
