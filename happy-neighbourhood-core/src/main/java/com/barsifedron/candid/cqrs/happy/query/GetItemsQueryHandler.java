package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.QLoan;
import com.barsifedron.candid.cqrs.happy.domain.QMember;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
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

        JPAQuery<Tuple> from = new JPAQueryFactory(entityManager)
                .select(item.id, item.name, item.since)
                .from(item);

        if (query.itemId != null) {
            from.where(item.id.id.eq(query.itemId));
        }

        List<Tuple> tuples = from.fetch();
        List<ItemId> foundItemIds = tuples.stream().map(tuple -> tuple.get(item.id)).collect(toList());

        Map<ItemId, List<LoanDto>> itemsLoans = new JPAQueryFactory(entityManager)

                .from(item, loan, member)
                .where(
                        item.id.in(foundItemIds),
                        item.id.eq(loan.itemId),
                        member.memberId.eq(loan.memberId))
                .orderBy(
                        item.since.desc(),
                        loan.borrowedOn.desc())
                .transform(groupBy(item.id).as(list(loanDtoProjection())));

        return tuples
                .stream()
                .map(tuple -> new ItemDto(
                        tuple.get(item.id).id(),
                        tuple.get(item.name),
                        tuple.get(item.since),
                        itemsLoans.getOrDefault(tuple.get(item.id), Collections.emptyList())))
                .sorted((dto1, dto2) -> dto2.since.compareTo(dto1.since))
                .collect(Collectors.toList());

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

    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ItemDto {

        public String id;
        public String name;
        public LocalDate since;

        public List<LoanDto> loansHistory;
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
