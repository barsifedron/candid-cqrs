package com.barsifedron.candid.app.items.domain;

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
public interface ItemsRepository {

    void add(Item item);

    Item get(ItemId id);
}
