package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.happy.domain.LoanId;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandResultToLog;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandToLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class BorrowItemCommand implements Command<LoanId>, CommandToLog, CommandResultToLog {

    @NotEmpty
    public  String loanId;

    @NotEmpty
    public  String memberId;

    @NotEmpty
    public  String itemId;

    @NotNull
    public  LocalDate borrowedOn;

    @NotNull
    public  BorrowItemCommandHandler.NOTIFICATION notification;

}
