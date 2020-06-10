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
public interface ItemsRepository {

    void add(List<Item> item);

    Item get(ItemId id);

    default void add(Item... items) {
        add(Stream.of(items).collect(toList()));
    }

    public static class InMemory implements ItemsRepository {

        private final Map<ItemId, Item> map;

        public InMemory() {
            this(new HashMap<>());
        }

        public InMemory(Map<ItemId, Item> map) {
            this.map = map;
        }

        @Override
        public void add(List<Item> items) {
            items.stream().forEach(item -> map.put(item.id(), item));
        }

        public Item get(ItemId id) {
            return map.get(id);
        }
    }
}
