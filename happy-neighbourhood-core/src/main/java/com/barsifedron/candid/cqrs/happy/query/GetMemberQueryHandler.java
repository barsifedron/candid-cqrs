package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;

import static com.barsifedron.candid.cqrs.happy.domain.QItem.item;
import static com.barsifedron.candid.cqrs.happy.domain.QLoan.loan;
import static com.barsifedron.candid.cqrs.happy.domain.QMember.member;

import com.barsifedron.candid.cqrs.query.QueryHandler;
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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static java.util.stream.Collectors.toList;

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

        JPAQuery<?> jpaQuery = new JPAQueryFactory(entityManager)
                .from(member)
                .leftJoin(loan)
                .on(member.memberId.eq(loan.memberId))
                .leftJoin(item)
                .on(item.id.eq(loan.itemId))
                .where(query.memberId != null ? member.memberId.id.eq(query.memberId) : null);

        Map<MemberId, MemberDto> membersWithLoans = jpaQuery
                .clone()
                .where(loan.id.isNotNull())
                .transform(groupBy(member.memberId).as(memberDtoProjection()));

        Map<MemberId, MemberDto> membersWithoutLoans = jpaQuery
                .clone()
                .where(loan.id.isNull())
                .transform(groupBy(member.memberId).as(loanLessMemberDtoProjection()));

        return Stream
                .of(membersWithLoans, membersWithoutLoans)
                .map(Map::values)
                .flatMap(Collection::stream)
                .sorted()
                .collect(toList());

    }

    private ConstructorExpression<MemberDto> memberDtoProjection() {
        return Projections.constructor(
                MemberDto.class,
                member.memberId.id,
                member.firstname,
                member.surname,
                member.email,
                member.registeredOn,
                list(Projections.constructor(
                        LoanDto.class,
                        loan.id.loanId,
                        item.id.id,
                        item.name,
                        loan.borrowedOn,
                        loan.effectiveReturnOn,
                        loan.status,
                        loan.detailedCosts))
        );
    }

    private ConstructorExpression<MemberDto> loanLessMemberDtoProjection() {
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

    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDto implements Comparable<MemberDto> {

        public String id;
        public String firstname;
        public String surname;
        public String email;
        public LocalDateTime registeredOn;
        public List<LoanDto> loansHistory;

        public MemberDto(String id, String firstname, String surname, String email, LocalDateTime registeredOn) {
            this.id = id;
            this.firstname = firstname;
            this.surname = surname;
            this.email = email;
            this.registeredOn = registeredOn;
        }

        @Override
        public int compareTo(MemberDto o) {
            return o.registeredOn.compareTo(registeredOn);
        }
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
