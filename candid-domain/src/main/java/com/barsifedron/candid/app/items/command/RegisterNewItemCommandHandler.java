package com.barsifedron.candid.app.items.command;


import com.barsifedron.candid.app.items.domain.Item;
import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemName;
import com.barsifedron.candid.app.items.domain.ItemsRepository;
import com.barsifedron.candid.app.items.domainevents.ItemRegistered;
import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Deals with the registering of a new item. I do not go crazy on domain validation here to keep things simple.
 */
public class RegisterNewItemCommandHandler implements CommandHandler<ItemId, RegisterNewItem> {

    private final ItemsRepository repository;

    @Inject
    public RegisterNewItemCommandHandler(ItemsRepository repository) {
        this.repository = repository;
    }

    @Override
    public CommandResponse<ItemId> handle(RegisterNewItem command) {

        Item newItem = new Item(new ItemName(command.name));

        // Simply add to your collections-like repository.
        // You should never deal with transactions at this level.
        // That is the job of the command bus middleware or, at the really worst, of the underlying repository implementation.
        // So no "update", "persist" etc...
        repository.add(newItem);

        // This is a LOCAL domain event, which can be listened to in order to create "side effects".
        // Not something to send to kafka, aws or others micro services.
        //
        // Send a welcome email, create an activity log, update a counter... none of these things should happen in your command handler.
        // Side effects will only ever be triggered through local domain events.
        // Good news : you have nothing to do but to add the domain events to the returned response. The command bus automatically dispatches them to their rightful handlers.
        // Note : we can generate more than one domain event. But also no event at all.
        DomainEvent domainEvent = new ItemRegistered(
                newItem.id(),
                new ItemName(command.name));
        return new CommandResponse<>(newItem.id(), domainEvent);
    }

    @Override
    public Class listenTo() {
        return RegisterNewItem.class;
    }
}
