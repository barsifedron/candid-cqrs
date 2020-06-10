package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandResultToLog;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandToLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class RegisterNewItemCommand implements Command<ItemId>, CommandToLog, CommandResultToLog {

    @NotEmpty
    public String id;

    @NotEmpty
    public String name;

    @NotNull
    @Min(0)
    @Max(15)
    public Integer maximumLoanPeriod;

    @NotNull
    @Min(0)
    public BigDecimal dailyRate;

    @Min(0)
    public BigDecimal dailyFineWhenLateReturn;
}
