package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandResultToLog;
import com.barsifedron.candid.cqrs.command.CommandToLog;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.happy.domain.LoanId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateNewLoanCommand implements Command<LoanId>, CommandToLog, CommandResultToLog {

    @NotEmpty
    String loanId;

    @NotEmpty
    String memberId;

    @NotEmpty
    String itemId;

    @NotNull
    LocalDate borrowedOn;

}
