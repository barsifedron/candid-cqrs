package com.barsifedron.candid.app.loan.command;

import com.barsifedron.candid.app.loan.domain.LoanId;
import com.barsifedron.candid.cqrs.command.Command;

import javax.validation.constraints.NotEmpty;

public class LendItemToMember implements Command<LoanId> {

    @NotEmpty
    public String itemId;

    @NotEmpty
    public String memberId;

    public LendItemToMember(String itemId, String memberId) {
        this.itemId = itemId;
        this.memberId = memberId;
    }
}
