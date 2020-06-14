package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.barsifedron.candid.cqrs.happy.domain.QItem.item;
import static com.barsifedron.candid.cqrs.happy.domain.QLoan.loan;
import static com.barsifedron.candid.cqrs.happy.domain.QMember.member;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static java.util.stream.Collectors.toList;

public class GetItemsQueryHandler implements QueryHandler<List<GetItemsQueryHandler.ItemDto>, GetItemsQuery> {

    private final EntityManager entityManager;

    @Inject
    public GetItemsQueryHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public List<ItemDto> handle(GetItemsQuery query) {

        Map<ItemId, ItemDto> items = new JPAQueryFactory(entityManager)
                .from(item)
                .leftJoin(loan)
                .on(item.id.eq(loan.itemId))
                .leftJoin(member)
                .on(member.memberId.eq(loan.memberId))
                .where(query.itemId == null ? null : item.id.id.eq(query.itemId))
                .transform(groupBy(item.id).as(itemDtoProjection()));

        return items.values().stream().collect(Collectors.toList());
    }

    private ConstructorExpression<ItemDto> itemDtoProjection() {
        return Projections.constructor(
                ItemDto.class,
                item.id.id,
                item.name,
                item.since,
                list(Projections.constructor(
                        LoanDto.class,
                        loan.id.loanId,
                        member.memberId.id,
                        member.firstname,
                        member.surname,
                        member.email,
                        loan.borrowedOn,
                        loan.effectiveReturnOn,
                        loan.status,
                        loan.detailedCosts)));
    }

    @Override
    public Class<GetItemsQuery> listenTo() {
        return GetItemsQuery.class;
    }

    @Builder(toBuilder = true)
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class ItemDto {

        public String id;
        public String name;
        public LocalDate since;

        public List<LoanDto> loansHistory;

        public ItemDto(String id, String name, LocalDate since) {
            this(id, name, since, Collections.emptyList());
        }

        public ItemDto(String id, String name, LocalDate since, List<LoanDto> loansHistory) {
            this.id = id;
            this.name = name;
            this.since = since;
            this.loansHistory = loansHistory.stream().filter(loanDto -> loanDto.loanId != null).collect(toList());
        }
    }

    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class LoanDto {

        public String loanId;
        public String memberId;
        public String memberFirstname;
        public String memberSurname;
        public String email;
        public LocalDate borrowedOn;
        public LocalDate returnedOn;
        public Loan.STATUS loanStatus;

        @ToString.Exclude
        public String detailedCosts;

    }
}
