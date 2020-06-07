package com.barsifedron.candid.cqrs.springboot.app.library.domain;

import java.util.UUID;

public class ThingId {

    private final String id;

    public ThingId() {
        this(UUID.randomUUID().toString());
    }

    public ThingId(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }


    @Override
    public String toString() {
        return "ThingId{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThingId thingId = (ThingId) o;

        return id != null ? id.equals(thingId.id) : thingId.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
