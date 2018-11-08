package com.barsifedron.candid.app.items.command;

import com.barsifedron.candid.app.items.domain.Item;
import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemName;
import com.barsifedron.candid.app.items.domain.ItemsRepository;
import com.barsifedron.candid.app.items.domainevents.ItemRegistered;
import com.barsifedron.candid.app.items.infrastructure.InMemoryItemsRepository;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import org.junit.Test;


import static org.junit.Assert.*;

public class RegisterNewItemCommandHandlerTest {

    @Test
    public void shouldRegisterAnItem() {
        ItemsRepository repository = new InMemoryItemsRepository();
        RegisterNewItemCommandHandler handler = new RegisterNewItemCommandHandler(repository);

        CommandResponse<ItemId> createHammerResponse = handler.handle(new RegisterNewItem("Hammer"));
        CommandResponse<ItemId> createScrewDriverResponse = handler.handle(new RegisterNewItem("Screw driver"));

        Item hammer = repository.get(createHammerResponse.result);
        assertTrue(hammer.hasName(new ItemName("Hammer")));
        assertFalse(hammer.hasName(new ItemName("Kitchen fork")));


        Item screwDriver = repository.get(createScrewDriverResponse.result);
        assertTrue(screwDriver.hasName(new ItemName("Screw driver")));
        assertFalse(screwDriver.hasName(new ItemName("Kitchen fork")));
    }

    @Test
    public void shouldPublishProperDomainEvents() {
        ItemsRepository repository = new InMemoryItemsRepository();
        RegisterNewItemCommandHandler handler = new RegisterNewItemCommandHandler(repository);

        CommandResponse<ItemId> response = handler.handle(new RegisterNewItem("Hammer"));
        DomainEvent domainEvent = response.events.get(0);

        assertTrue(domainEvent.getClass().isAssignableFrom(ItemRegistered.class));
        assertEquals(new ItemName("Hammer"), ((ItemRegistered) domainEvent).name);
        assertEquals(response.result, ((ItemRegistered) domainEvent).itemId);
    }
}