package com.barsifedron.candid.cqrs.happy.infrastructure;

import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import com.barsifedron.candid.cqrs.happy.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class HibernateMemberRepository implements MembersRepository {

    private final EntityManager entityManager;

    @Inject
    public HibernateMemberRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Member get(MemberId memberId) {
        return new JPAQueryFactory(entityManager)
                .select(QMember.member)
                .from(QMember.member)
                .where(QMember.member.memberId.eq(memberId))
                .fetchOne();
    }

    @Override
    public Member withEmail(String email) {
        return new JPAQueryFactory(entityManager)
                .select(QMember.member)
                .from(QMember.member)
                .where(QMember.member.email.eq(email))
                .fetchOne();
    }

    @Override
    public void add(Member member) {
        entityManager.persist(member);
    }

}
