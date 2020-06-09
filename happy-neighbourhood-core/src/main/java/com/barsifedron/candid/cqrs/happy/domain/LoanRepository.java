package com.barsifedron.candid.cqrs.happy.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public interface LoanRepository {

    Loan get(LoanId toString);

    void add(Loan loan);

    List<Loan> forMember(MemberId memberId, Loan.STATUS status);

    List<Loan> forItem(ItemId itemId, Loan.STATUS status);

    default void add(Loan... loans) {
        Stream.of(loans).forEach(this::add);
    }

    public static class InMemory implements LoanRepository {

        private final Map<LoanId, Loan> map;

        public InMemory() {
            this(new HashMap<>());
        }

        public InMemory(Map<LoanId, Loan> map) {
            this.map = map;
        }

        @Override
        public Loan get(LoanId id) {
            return Optional
                    .ofNullable(map.get(id))
                    .orElseThrow(() -> new RuntimeException("Item not found id : " + id));
        }

        @Override
        public void add(Loan loan) {
            map.put(loan.id(), loan);
        }

        @Override
        public List<Loan> forMember(MemberId memberId, Loan.STATUS status) {
            return map
                    .values()
                    .stream()
                    .filter(loan -> loan.hasMemberId(memberId))
                    .filter(loan -> loan.hasStatus(status))
                    .collect(toList());

        }

        @Override
        public List<Loan> forItem(ItemId itemId, Loan.STATUS status) {
            return map
                    .values()
                    .stream()
                    .filter(loan -> loan.hasItemId(itemId))
                    .filter(loan -> loan.hasStatus(status))
                    .collect(toList());
        }
    }

}

