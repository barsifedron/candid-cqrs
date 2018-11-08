package com.barsifedron.candid.app.loan.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.NoResult;

import javax.validation.constraints.NotBlank;

public class ReturnItem implements Command<NoResult> {

    @NotBlank
    public String itemId;

    public ReturnItem(String itemId) {
        this.itemId = itemId;
    }
}
