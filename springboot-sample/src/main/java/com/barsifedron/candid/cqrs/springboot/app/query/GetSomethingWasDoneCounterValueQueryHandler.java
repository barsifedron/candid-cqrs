package com.barsifedron.candid.cqrs.springboot.app.query;

import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.QMember;
import com.barsifedron.candid.cqrs.happy.domain.QStudent;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.ThingsDoneCounter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

public class GetSomethingWasDoneCounterValueQueryHandler
        implements QueryHandler<Long, GetSomethingWasDoneCounterValueQuery> {

    private final JPAQueryFactory jpaQueryFactory;

    private final EntityManager entityManager;

    @Autowired
    public GetSomethingWasDoneCounterValueQueryHandler( EntityManager entityManager) {
        this.entityManager = entityManager;
        jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Long handle(GetSomethingWasDoneCounterValueQuery query) {

        System.out.println("jpaQueryFactory = " + jpaQueryFactory);

        //        entityManager.getTransaction().begin();
        Object where = jpaQueryFactory
                .select(QStudent.student.name)
                .from(QStudent.student)
                //                .where(QStudent.student.id.eq(Long.valueOf("1")))
                .fetch();

        System.out.println("where = " + where);

        System.out.println("entityManager = " + entityManager);

        List<Member> members = jpaQueryFactory
                .select(QMember.member)
                .from(QMember.member)
                .fetch();
        System.out.println("members = " + members);

        return new ThingsDoneCounter().value();
    }

    @Override
    public Class<GetSomethingWasDoneCounterValueQuery> listenTo() {
        return GetSomethingWasDoneCounterValueQuery.class;
    }
}
