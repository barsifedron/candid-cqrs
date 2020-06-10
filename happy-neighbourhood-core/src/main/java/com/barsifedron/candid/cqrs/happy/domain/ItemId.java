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
public class ItemId implements Serializable {

    @Column(name = "id", nullable = false, unique = true)
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

}
