package com.barsifedron.candid.app.items.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.NoResult;

import javax.validation.constraints.NotBlank;

public class RenameItem implements Command<NoResult> {

    @NotBlank(message = "Needs a valid id")
    public String itemId;

    @NotBlank(message = "Needs a valid name")
    public String newItemName;

    public RenameItem(String itemId, String newItemName) {
        this.itemId = itemId;
        this.newItemName = newItemName;
    }
}
