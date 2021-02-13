package com.barsifedron.candid.cqrs.happy.domainevents;

import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.barsifedron.candid.cqrs.happy.domain.Counter;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.ItemsCounterRepository;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * Increments a counter keeping track of a the number of times an Item is borrowed each day
 */
public class ItemBorrowedDomainEventHandler_FavoriteCount implements DomainEventHandler<ItemBorrowedDomainEvent> {

    private final ItemsCounterRepository itemsCounterRepository;

    @Inject
    public ItemBorrowedDomainEventHandler_FavoriteCount(ItemsCounterRepository counterRepository) {
        this.itemsCounterRepository = counterRepository;
    }

    @Override
    public void handle(ItemBorrowedDomainEvent event) {
        ItemId itemId = new ItemId(event.itemId);
        Counter counter = itemsCounterRepository.dailyCounter(LocalDate.now(), itemId);
        if (counter == null) {
            // first borrowing of the day. Create a new counter for this item.
            itemsCounterRepository.save(new Counter(itemId, 1, LocalDate.now()));
            return;
        }
        counter.increment();
    }

    @Override
    public Class<ItemBorrowedDomainEvent> listenTo() {
        return ItemBorrowedDomainEvent.class;
    }

}
