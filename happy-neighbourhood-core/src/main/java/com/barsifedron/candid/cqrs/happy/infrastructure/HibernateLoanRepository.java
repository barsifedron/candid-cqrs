package com.barsifedron.candid.cqrs.happy.infrastructure;

import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.LoanId;
import com.barsifedron.candid.cqrs.happy.domain.LoanRepository;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.domain.QLoan;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class HibernateLoanRepository implements LoanRepository {

    private final EntityManager entityManager;

    @Inject
    public HibernateLoanRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void add(Loan loan) {
        entityManager.persist(loan);
    }

    @Override
    public Loan get(LoanId id) {
        return new JPAQueryFactory(entityManager)
                .select(QLoan.loan)
                .from(QLoan.loan)
                .where(QLoan.loan.id.eq(id))
                .fetchOne();
    }

    @Override
    public List<Loan> forMember(MemberId memberId, Loan.STATUS... statuses) {
        List<Loan> fetch = new JPAQueryFactory(entityManager)
                .select(QLoan.loan)
                .from(QLoan.loan)
                .where(
                        QLoan.loan.status.in(statuses),
                        QLoan.loan.memberId.eq(memberId))
                .fetch();
        return fetch;
    }

    @Override
    public List<Loan> forItem(ItemId itemId, Loan.STATUS... statuses) {
        return new JPAQueryFactory(entityManager)
                .select(QLoan.loan)
                .from(QLoan.loan)
                .where(
                        QLoan.loan.status.in(statuses),
                        QLoan.loan.itemId.eq(itemId))
                .fetch();
    }

    @Override
    public List<Loan> all() {
        return new JPAQueryFactory(entityManager)
                .select(QLoan.loan)
                .from(QLoan.loan)
                .fetch();
    }
}
