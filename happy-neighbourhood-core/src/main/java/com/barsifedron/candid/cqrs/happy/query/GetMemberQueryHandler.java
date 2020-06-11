package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.domain.QItem;
import com.barsifedron.candid.cqrs.happy.domain.QLoan;
import com.barsifedron.candid.cqrs.happy.domain.QMember;

import static com.barsifedron.candid.cqrs.happy.domain.QItem.item;
import static com.barsifedron.candid.cqrs.happy.domain.QLoan.loan;
import static com.barsifedron.candid.cqrs.happy.domain.QMember.member;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;
import static java.util.Collections.emptyList;

public class GetMemberQueryHandler
        implements QueryHandler<Collection<GetMemberQueryHandler.MemberDto>, GetMemberQuery> {

    private final EntityManager entityManager;

    @Inject
    public GetMemberQueryHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Collection<MemberDto> handle(GetMemberQuery query) {

        Map<MemberId, MemberDto> members = new JPAQueryFactory(entityManager)
                .from(member)
                .where(query.memberId != null ? member.memberId.id.eq(query.memberId) : null)
                .orderBy(member.registeredOn.desc())
                .transform(groupBy(member.memberId).as(memberDtoProjection()));

        Map<MemberId, List<LoanDto>> loans = new JPAQueryFactory(entityManager)
                .from(
                        item,
                        loan,
                        member
                )
                .where(
                        item.id.eq(loan.itemId),
                        member.memberId.in(members.keySet()),
                        member.memberId.eq(loan.memberId)
                )
                .orderBy(
                        member.registeredOn.desc(),
                        loan.borrowedOn.desc()
                )
                .transform(groupBy(member.memberId).as(GroupBy.list(loanProjection())));

        members
                .keySet()
                .forEach(memberId -> members.get(memberId).loansHistory = loans.getOrDefault(memberId, emptyList()));

        return members.values();

    }

    private ConstructorExpression<LoanDto> loanProjection() {
        return Projections.constructor(
                LoanDto.class,
                loan.id.loanId,
                item.id.id,
                item.name,
                loan.borrowedOn,
                loan.effectiveReturnOn,
                loan.status,
                loan.bill);
    }

    private ConstructorExpression<MemberDto> memberDtoProjection() {
        return Projections.constructor(
                MemberDto.class,
                member.memberId.id,
                member.firstname,
                member.surname,
                member.email,
                member.registeredOn
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

        public MemberDto(String id, String firstname, String surname, String email, LocalDateTime registeredOn) {
            this.id = id;
            this.firstname = firstname;
            this.surname = surname;
            this.email = email;
            this.registeredOn = registeredOn;
        }

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
