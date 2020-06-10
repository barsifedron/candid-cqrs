package com.barsifedron.candid.cqrs.happy.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@ToString
@Embeddable
@EqualsAndHashCode
@Access(AccessType.FIELD)
public class MemberId implements Serializable {

    @Column(name = "id", nullable = false)
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

}
