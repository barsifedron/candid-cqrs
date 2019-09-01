package com.barsifedron.candid.app.items.command;

import com.barsifedron.candid.app.items.domain.Item;
import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemName;
import com.barsifedron.candid.app.items.domain.ItemsRepository;
import com.barsifedron.candid.app.items.domainevents.ItemRenamed;
import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import javax.inject.Inject;

public class RenameItemCommandHandler implements CommandHandler<NoResult, RenameItem> {

    private final ItemsRepository repository;

    @Inject
    public RenameItemCommandHandler(ItemsRepository repository) {
        this.repository = repository;
    }

    @Override
    public CommandResponse<NoResult> handle(RenameItem command) {

        Item itemToRename = repository.get(new ItemId(command.itemId));
        itemToRename.renameAs(command.newItemName);

        // This is a LOCAL domain event, which can be listened to in order to create "side effects".
        // Not something to send to kafka, aws or others micro services.
        //
        // Send a welcome email, create an activity log, update a counter... none of these things should happen in your command handler.
        // Side effects will only ever be triggered through local domain events.
        // Good news : you have nothing to do but to add the domain events to the returned response. The command bus automatically dispatches them to their rightful handlers.
        // Note : we can generate more than one domain event. But also no event at all.
        DomainEvent domainEvent = new ItemRenamed(
                itemToRename.id(),
                new ItemName(command.newItemName));
        return CommandResponse.empty().withAddedDomainEvents(domainEvent);
    }

    @Override
    public Class listenTo() {
        return RenameItem.class;
    }
}
