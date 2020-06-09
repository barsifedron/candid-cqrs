package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandResultToLog;
import com.barsifedron.candid.cqrs.command.CommandToLog;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegisterNewItemCommand implements Command<ItemId>, CommandToLog, CommandResultToLog {

    @NotEmpty
    public final String id;

    @NotEmpty
    public final String name;

    @NotNull
    @Min(0)
    @Max(15)
    public final Integer maximumLoanPeriod;

    @NotNull
    @Min(0)
    public final BigDecimal dailyRate;

    @Min(0)
    public final BigDecimal dailyFineWhenLateReturn;
}
