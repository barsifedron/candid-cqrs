package com.barsifedron.candid.app.items.command;

import com.barsifedron.candid.app.items.domain.Item;
import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemName;
import com.barsifedron.candid.app.items.domain.ItemsRepository;
import com.barsifedron.candid.app.items.domainevents.ItemRenamed;
import com.barsifedron.candid.app.items.infrastructure.InMemoryItemsRepository;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class RenameItemCommandHandlerTest {


    @Test
    public void shouldRename() {

        ItemId itemId = new ItemId("itemId");
        ItemsRepository repository = new InMemoryItemsRepository();
        repository.add(new Item(
                itemId,
                new ItemName("Hammer"),
                LocalDate.now()));

        RenameItemCommandHandler renameHandler = new RenameItemCommandHandler(repository);
        renameHandler.handle(new RenameItem(
                itemId.id(),
                "WarHammer"));

        Item expectedHammer = repository.get(itemId);

        assertTrue(expectedHammer.hasName(new ItemName("WarHammer")));
        assertFalse(expectedHammer.hasName(new ItemName("Hammer")));
        assertFalse(expectedHammer.hasName(new ItemName("Kitchen fork")));
    }

    @Test
    public void shouldPublishProperEvents() {

        ItemId itemId = new ItemId("itemId");
        ItemsRepository repository = new InMemoryItemsRepository();
        repository.add(new Item(
                itemId,
                new ItemName("Hammer"),
                LocalDate.now()));

        RenameItemCommandHandler renameHandler = new RenameItemCommandHandler(repository);
        CommandResponse<NoResult> response = renameHandler.handle(new RenameItem(
                itemId.id(),
                "WarHammer"));

        assertFalse(response.domainEvents.isEmpty());
        DomainEvent publishedEvent = response.domainEvents.get(0);
        assertTrue(publishedEvent.getClass().isAssignableFrom(ItemRenamed.class));
        assertEquals(new ItemName("WarHammer"), ((ItemRenamed) publishedEvent).itemNewName);
        assertEquals(itemId, ((ItemRenamed) publishedEvent).itemId);
    }
}