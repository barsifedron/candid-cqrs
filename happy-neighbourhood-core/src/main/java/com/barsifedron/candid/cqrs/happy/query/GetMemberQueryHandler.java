package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.domain.QItem;
import com.barsifedron.candid.cqrs.happy.domain.QLoan;
import com.barsifedron.candid.cqrs.happy.domain.QMember;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class GetMemberQueryHandler implements QueryHandler<GetMemberQueryHandler.MemberDto, GetMemberQuery> {

    private final EntityManager entityManager;

    @Inject
    public GetMemberQueryHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public MemberDto handle(GetMemberQuery query) {

        Tuple tuple = new JPAQueryFactory(entityManager)
                .select(
                        QMember.member.memberId.id,
                        QMember.member.firstname,
                        QMember.member.surname,
                        QMember.member.email,
                        QMember.member.registeredOn
                )
                .from(QMember.member)
                .where(QMember.member.memberId.id.eq(query.memberId))
                .fetchOne();

        List<LoanDto> loanHistory = new JPAQueryFactory(entityManager)

                .select(
                        Projections.constructor(
                                LoanDto.class,
                                QLoan.loan.id.loanId,
                                QItem.item.id.id,
                                QItem.item.name,
                                QLoan.loan.borrowedOn,
                                QLoan.loan.effectiveReturnOn,
                                QLoan.loan.status,
                                QLoan.loan.bill)
                )
                .from(QMember.member, QLoan.loan, QItem.item)
                .where(
                        QItem.item.id.eq(QLoan.loan.itemId),
                        QMember.member.memberId.id.eq(query.memberId),
                        QMember.member.memberId.eq(QLoan.loan.memberId))

                .orderBy(QLoan.loan.borrowedOn.desc())

                .fetch();

        return new MemberDto(
                tuple.get(QMember.member.memberId.id),
                tuple.get(QMember.member.firstname),
                tuple.get(QMember.member.surname),
                tuple.get(QMember.member.email),
                tuple.get(QMember.member.registeredOn),
                loanHistory
        );
    }

    @Override
    public Class<GetMemberQuery> listenTo() {
        return GetMemberQuery.class;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class MemberDto {

        public String id;
        public String firstname;
        public String surname;
        public String email;
        public LocalDateTime registeredOn;

        public List<LoanDto> loansHistory;
    }

    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanDto {

        public String loanId;
        public String itemId;
        public String itemName;
        public LocalDate borrowedOn;
        public LocalDate returnedOn;
        public Loan.STATUS loanStatus;
        public String bill;
    }
}
