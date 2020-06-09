package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.happy.domain.Item;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.ItemsRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RegisterNewItemCommandHandlerTest {

    @Test
    void shouldFailIdItemIdExists() {

        ItemsRepository.InMemory items = new ItemsRepository.InMemory();

        items.add(Item
                .builder()
                .id(new ItemId("iAlreadyExists"))
                .build());

        RegisterNewItemCommand command = RegisterNewItemCommand
                .builder()
                .id("iAlreadyExists")
                .build();

        RegisterNewItemCommandHandler handler = new RegisterNewItemCommandHandler(items);

        assertThrows(
                RegisterNewItemCommandHandler.ItemIdAlreadyInUseException.class,
                () -> handler.handle(command));

    }

    @Test
    void shouldCreateProperItem() {

        ItemsRepository.InMemory items = new ItemsRepository.InMemory();

        RegisterNewItemCommand command = RegisterNewItemCommand
                .builder()
                .name("Hammer")
                .maximumLoanPeriod(10)
                .id(new ItemId("hammer").id())
                .dailyRate(new BigDecimal("1.0"))
                .dailyFineWhenLateReturn(new BigDecimal("1.0"))
                .build();

        RegisterNewItemCommandHandler handler = new RegisterNewItemCommandHandler(items);

        CommandResponse<ItemId> commandResponse = assertDoesNotThrow(() -> handler.handle(command));

        assertEquals(new ItemId("hammer"), commandResponse.result);

        Item hammer = items.get(new ItemId("hammer"));

        assertTrue(hammer.hasId(new ItemId("hammer")));
        assertTrue(hammer.hasName("Hammer"));
        assertTrue(hammer.hasMaximumLoanPeriod(10));
        assertTrue(hammer.hasDailyRate(new BigDecimal("1.0")));
        assertTrue(hammer.hasDailyFineWhenLateReturn(new BigDecimal("1.0")));
    }
}