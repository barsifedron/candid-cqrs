package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.query.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class GetPeriodReportQuery implements Query<GetPeriodReportQueryHandler.ReportDto> {

    @NotNull
    public LocalDate periodStartDate;

    @NotNull
    public LocalDate periodEndDate;

}
