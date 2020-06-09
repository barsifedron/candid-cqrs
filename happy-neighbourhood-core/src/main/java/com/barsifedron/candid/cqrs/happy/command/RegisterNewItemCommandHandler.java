package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.happy.domain.Item;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.ItemsRepository;

import javax.inject.Inject;
import java.time.LocalDate;

public class RegisterNewItemCommandHandler implements CommandHandler<ItemId, RegisterNewItemCommand> {

    private final ItemsRepository items;

    @Inject
    public RegisterNewItemCommandHandler(ItemsRepository items) {
        this.items = items;
    }

    @Override
    public CommandResponse<ItemId> handle(RegisterNewItemCommand command) {

        Item shouldNotExist = items.get(new ItemId(command.id));
        if (shouldNotExist != null) {
            throw new ItemIdAlreadyInUseException(command.id);
        }

        Item newItem = Item
                .builder()
                .name(command.name)
                .id(new ItemId(command.id))
                .dailyRate(command.dailyRate)
                .maximumLoanPeriod(command.maximumLoanPeriod)
                .dailyFineWhenLateReturn(command.dailyFineWhenLateReturn)
                .since(LocalDate.now())
                .build();

        items.add(newItem);

        return CommandResponse
                .empty()
                .withResult(newItem.id());
    }

    @Override
    public Class<RegisterNewItemCommand> listenTo() {
        return RegisterNewItemCommand.class;
    }

    public static class ItemIdAlreadyInUseException extends RuntimeException {
        public ItemIdAlreadyInUseException(String memberId) {
            super(String.format("The member id %s is already assigned to another user.", memberId));
        }
    }

}
