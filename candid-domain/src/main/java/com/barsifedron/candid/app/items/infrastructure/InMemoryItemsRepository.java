package com.barsifedron.candid.app.items.infrastructure;

import com.barsifedron.candid.app.items.domain.Item;
import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemsRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryItemsRepository implements ItemsRepository {

    private final Map<ItemId, Item> map;

    public InMemoryItemsRepository() {
        this(new HashMap<>());
    }

    public InMemoryItemsRepository(Map<ItemId, Item> map) {
        this.map = map;
    }

    @Override
    public void add(Item item) {
        map.put(item.id(), item);
    }

    public Item get(ItemId id) {
        return Optional
                .ofNullable(map.get(id))
                .orElseThrow(() -> new RuntimeException("Item not found id : " + id));
    }
}
