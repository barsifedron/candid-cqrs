package com.barsifedron.candid.cqrs.happy.infrastructure;

import com.barsifedron.candid.cqrs.happy.domain.Email;
import com.barsifedron.candid.cqrs.happy.domain.EmailRepository;
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

public class HibernateEmailRepository implements EmailRepository {

    private final EntityManager entityManager;

    @Inject
    public HibernateEmailRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void add(Email email) {
        entityManager.persist(email);
    }

}
