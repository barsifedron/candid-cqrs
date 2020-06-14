package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.LoanCost;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.barsifedron.candid.cqrs.happy.domain.QItem.item;
import static com.barsifedron.candid.cqrs.happy.domain.QLoan.loan;
import static com.querydsl.core.group.GroupBy.list;
import static java.util.stream.Collectors.toList;

public class GetPeriodReportQueryHandler
        implements QueryHandler<GetPeriodReportQueryHandler.ReportDto, GetPeriodReportQuery> {

    private final EntityManager entityManager;

    @Inject
    public GetPeriodReportQueryHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ReportDto handle(GetPeriodReportQuery query) {

        if (query.periodStartDate.isAfter(query.periodEndDate)) {
            throw new IllegalArgumentException("Search end date can not be before start date.");
        }

        Map<ItemId, Group> periodLoans = new JPAQueryFactory(entityManager)
                .from(loan, item)
                .where(
                        loan.effectiveReturnOn.isNotNull(),
                        loan.borrowedOn.after(query.periodStartDate.minusDays(1)),
                        loan.effectiveReturnOn.before(query.periodEndDate.plusDays(1)))
                .transform(GroupBy
                        .groupBy(loan.itemId)
                        .as(
                                list(loan),
                                item.name));

        List<ItemStatisticsDto> itemStatistics = periodLoans
                .keySet()
                .stream()
                .map(itemId -> {

                    Group group = periodLoans.get(itemId);

                    int itemNumberOfLoans = group.getList(loan).size();

                    LoanCost itemTotalLoanCost = group
                            .getList(loan)
                            .stream()
                            .map(loan -> loan.fullLoanCostShort())
                            .reduce(LoanCost.NO_COST(), LoanCost::add);

                    LoanCost itemTotalPenalties = group
                            .getList(loan)
                            .stream()
                            .map(loan -> loan.loanCostPenaltiesPart())
                            .reduce(LoanCost.NO_COST(), LoanCost::add);

                    return new ItemStatisticsDto(
                            itemId.id(),
                            group.getOne(item.name),
                            Long.valueOf(itemNumberOfLoans),
                            itemTotalLoanCost.cost(),
                            itemTotalPenalties.cost());

                })
                // Display the items generating more revenue first
                .sorted((firstItemStats, secondItemStats) -> secondItemStats.totalLoanCosts.compareTo(firstItemStats.totalLoanCosts))
                .collect(toList());

        BigDecimal totalLoansCost = itemStatistics
                .stream()
                .map(dto -> dto.totalLoanCosts)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPenalties = itemStatistics
                .stream()
                .map(dto -> dto.totalPenalties)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalNumberOfLoans = itemStatistics
                .stream()
                .map(dto -> dto.numberOfLoans)
                .reduce((long) 0, (a, b) -> a + b);

        return ReportDto
                .builder()
                .numberOfLoans(totalNumberOfLoans)
                .totalLoanCosts(totalLoansCost)
                .periodStartDate(query.periodStartDate)
                .periodEndDate(query.periodEndDate)
                .totalPenalties(totalPenalties)
                .itemsStatistics(itemStatistics)
                .build();

    }

    @Override
    public Class listenTo() {
        return GetPeriodReportQuery.class;
    }

    // the details for the whole period
    @Builder
    public static class ReportDto {

        public Long numberOfLoans;
        public LocalDate periodStartDate;
        public LocalDate periodEndDate;

        public BigDecimal totalLoanCosts;
        public BigDecimal totalPenalties;

        public Collection<ItemStatisticsDto> itemsStatistics;

    }

    // the details for each item
    @AllArgsConstructor
    public static class ItemStatisticsDto {

        public String itemId;
        public String itemName;
        public Long numberOfLoans;
        public BigDecimal totalLoanCosts;
        public BigDecimal totalPenalties;

    }
}
