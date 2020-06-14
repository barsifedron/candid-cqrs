package com.barsifedron.candid.cqrs.happy.shell.controllers;

import com.barsifedron.candid.cqrs.happy.query.GetItemsQueryHandler;
import com.barsifedron.candid.cqrs.happy.query.GetPeriodReportQuery;
import com.barsifedron.candid.cqrs.happy.query.GetPeriodReportQueryHandler;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.query.QueryBusFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    private final QueryBusFactory queryBusFactory;

    @Inject
    public ReportsController(QueryBusFactory queryBusFactory) {
        this.queryBusFactory = queryBusFactory;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<GetItemsQueryHandler.ItemDto>> getReports(
            @RequestParam String periodStartDate,
            @RequestParam String periodEndDate) {

        GetPeriodReportQueryHandler.ReportDto reportDto = queryBusFactory.simpleBus().dispatch(GetPeriodReportQuery
                .builder()
                .periodStartDate(LocalDate.parse(periodStartDate))
                .periodEndDate(LocalDate.parse(periodEndDate))
                .build());

        return new ResponseEntity(reportDto, HttpStatus.OK);
    }

}
