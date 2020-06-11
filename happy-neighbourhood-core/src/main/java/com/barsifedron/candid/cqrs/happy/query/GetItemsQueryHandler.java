package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
import static java.util.Collections.emptyList;
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
                .where(query.itemId != null ? item.id.id.eq(query.itemId) : null)
                .orderBy(item.since.desc())
                .transform(groupBy(item.id).as(itemDtoProjection()));

        Map<ItemId, List<LoanDto>> loans = new JPAQueryFactory(entityManager)
                .from(item, loan, member)
                .where(
                        item.id.in(items.keySet()),
                        item.id.eq(loan.itemId),
                        member.memberId.eq(loan.memberId))
                .orderBy(
                        item.since.desc(),
                        loan.borrowedOn.desc())
                .transform(groupBy(item.id).as(list(loanDtoProjection())));

        items
                .keySet()
                .forEach(itemId -> items.get(itemId).loansHistory = loans.getOrDefault(itemId, emptyList()));

        return items.values().stream().collect(toList());

    }

    private ConstructorExpression<ItemDto> itemDtoProjection() {
        return Projections.constructor(
                ItemDto.class,
                item.id.id,
                item.name,
                item.since);
    }

    private ConstructorExpression<LoanDto> loanDtoProjection() {
        return Projections.constructor(
                LoanDto.class,
                loan.id.loanId,
                member.memberId.id,
                member.firstname,
                member.surname,
                member.email,
                loan.borrowedOn,
                loan.effectiveReturnOn,
                loan.status,
                loan.bill);
    }

    @Override
    public Class<GetItemsQuery> listenTo() {
        return GetItemsQuery.class;
    }

    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ItemDto {

        public String id;
        public String name;
        public LocalDate since;

        public List<LoanDto> loansHistory;

        public ItemDto(String id, String name, LocalDate since) {
            this.id = id;
            this.name = name;
            this.since = since;
        }
    }

    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanDto {

        public String loanId;
        public String memberId;
        public String memberFirstname;
        public String memberSurname;
        public String email;
        public LocalDate borrowedOn;
        public LocalDate returnedOn;
        public Loan.STATUS loanStatus;
        public String bill;
    }
}
