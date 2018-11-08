package com.barsifedron.candid.app.loan.domain;

import com.barsifedron.candid.app.items.domain.ItemId;

/**
 * Your repository interfaces should emulate a collection.
 * <p>
 * You should never deal with transactions at this interface level.
 * That is the job of the command bus middleware or, at the really worst, of the underlying repository implementation.
 * So no "update", "persist" etc...
 * <p>
 * You have to consider this as a WRITE repository and avoid to pollute it with methods used by the READ side.
 * This is a key point of CQRS that helps keep your repositories and application simple.
 * The READ side should read directly from projection tables or handle its own sql queries.
 */
public interface LoanRepository {

    Loan get(LoanId toString);

    void add(Loan loan);

    /**
     * Example of Read side "pollution".
     * This is not necessarily wrong per se. But you should at the very least consider the possibility to remove this method from this interface
     * and have whoever is calling it handle it differently through read side queries.
     * <p>
     * Of course, it is not always practical. As usual, it is a matter of trade-offs.
     */
    Loan getActiveLoan(ItemId toString);
}

