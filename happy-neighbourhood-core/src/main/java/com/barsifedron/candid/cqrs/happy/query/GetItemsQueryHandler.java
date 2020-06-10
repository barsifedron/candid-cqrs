package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.domain.Item;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.QItem;
import com.barsifedron.candid.cqrs.happy.domain.QLoan;
import com.barsifedron.candid.cqrs.happy.domain.QMember;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class GetItemQueryHandler implements QueryHandler<GetItemQueryHandler.ItemDto, GetItemQuery> {

    private final EntityManager entityManager;

    @Inject
    public GetItemQueryHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ItemDto handle(GetItemQuery query) {

        System.out.println("query = " + query);

        Tuple tuple = new JPAQueryFactory(entityManager)
                .select(
                        QItem.item.id.id,
                        QItem.item.name,
                        QItem.item.since
                )
                .from(QItem.item)
                .where(QItem.item.id.id.eq(query.itemId))
                .fetchOne();

        System.out.println("tuple = " + tuple);

        List<LoanDto> loanHistory = new JPAQueryFactory(entityManager)

                .select(
                        Projections.constructor(
                                LoanDto.class,
                                QLoan.loan.id.loanId,
                                QMember.member.memberId.id,
                                QMember.member.firstname,
                                QMember.member.surname,
                                QMember.member.email,
                                QLoan.loan.borrowedOn,
                                QLoan.loan.effectiveReturnOn,
                                QLoan.loan.status,
                                QLoan.loan.bill)
                )
                .from(QMember.member, QLoan.loan, QItem.item)
                .where(
                        QItem.item.id.id.eq(query.itemId),
                        QItem.item.id.eq(QLoan.loan.itemId),
                        QMember.member.memberId.eq(QLoan.loan.memberId))

                .orderBy(QLoan.loan.borrowedOn.desc())

                .fetch();

        return new ItemDto(
                tuple.get(QItem.item.id.id),
                tuple.get(QItem.item.name),
                tuple.get(QItem.item.since),
                loanHistory
        );
    }

    @Override
    public Class<GetItemQuery> listenTo() {
        return GetItemQuery.class;
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
