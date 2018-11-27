package com.barsifedron.candid.app.items.command;

import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.cqrs.command.Command;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

public class RegisterNewItem implements Command<ItemId> {

    @NotBlank(message = "Needs a valid name")
    public String name;

    public RegisterNewItem(String name) {
        this.name = name;
    }

}
