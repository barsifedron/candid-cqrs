package com.barsifedron.candid.cqrs.happy.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public interface EmailRepository {

    void add(Email email);

    default void add(Email... emails) {
        Stream
                .of(emails)
                .forEach(this::add);
    }

    public static class InMemory implements EmailRepository {

        private Map<Integer, Email> map = new HashMap<>();

        @Override
        public void add(Email email) {
            map.put(email.id(), email);
        }
    }

}
