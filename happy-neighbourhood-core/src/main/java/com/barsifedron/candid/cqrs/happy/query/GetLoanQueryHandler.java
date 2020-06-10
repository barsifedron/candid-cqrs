package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.QItem;
import com.barsifedron.candid.cqrs.happy.domain.QLoan;
import com.barsifedron.candid.cqrs.happy.domain.QMember;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GetLoanQueryHandler implements QueryHandler<List<GetLoanQueryHandler.LoanDto>, GetLoanQuery> {

    private final EntityManager entityManager;

    @Inject
    public GetLoanQueryHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<LoanDto> handle(GetLoanQuery query) {

        List<Tuple> fetch = new JPAQueryFactory(entityManager)
                .select(QLoan.loan, QItem.item.name, QMember.member.email)
                .from(QLoan.loan, QMember.member, QItem.item)
                .where(
                        QItem.item.id.eq(QLoan.loan.itemId),
                        QMember.member.memberId.eq(QLoan.loan.memberId))
                .fetch();

        fetch
                .stream()
                .map(tuple -> tuple.get(QLoan.loan))
                .map(loan -> {
                    return loan.loanCost();
                });

        return null;
    }

    @Override
    public Class<GetLoanQuery> listenTo() {
        return GetLoanQuery.class;
    }

    @Builder(toBuilder = true)
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class LoanDto {

        String itemName;
        String memberFirstname;
        String memberSurname;
        String memberEmail;
        String itemId;
        String memberId;

        LocalDate borrowedOn;
        LocalDate expectedReturnOn;
        LocalDate effectiveReturnOn;

        BigDecimal regularDailyRate;

        BigDecimal dailyFineWhenLate;

        String borrowingCost;

    }
}
