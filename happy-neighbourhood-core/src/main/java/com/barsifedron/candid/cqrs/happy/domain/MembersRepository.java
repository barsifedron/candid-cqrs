package com.barsifedron.candid.cqrs.happy.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Your repository interfaces should emulate a collection.
 * <p>
 * You should never deal with transactions at this interface level.
 * That is the job of the command bus middleware or, at the really worst, of the underlying repository implementation.
 * So no "update", "persist" etc...
 * <p>
 * You have to consider this as a WRITE repository and avoid to pollute it with methods used by the READ side.
 * This is a key point of CQRS that helps keep your repositories and application simple.
 * The READ side should read directly from the db with sql queries or projection tables etc... whatever works really
 */
public interface MembersRepository {

    void add(Member member);

    Member get(MemberId memberId);

    Member withEmail(String email);

    default void add(Member... members) {
        Stream.of(members).forEach(this::add);
    }

    /**
     * An In memory implementation for tests
     */
    class InMemory implements MembersRepository {

        private Map<MemberId, Member> map = new HashMap<>();

        public Member withEmail(String email) {
            return map
                    .values()
                    .stream()
                    .filter(member -> member.hasEmail(email))
                    .findAny()
                    .orElse(null);
        }

        @Override
        public Member get(MemberId memberId) {
            return map.get(memberId);
        }

        @Override
        public void add(Member member) {
            map.put(member.memberId(), member);
        }
    }

}
