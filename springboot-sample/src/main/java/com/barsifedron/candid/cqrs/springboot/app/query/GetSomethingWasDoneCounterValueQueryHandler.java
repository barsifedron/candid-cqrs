package com.barsifedron.candid.cqrs.springboot.app.query;

import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.QStudent;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.Student;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.StudentRepository;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.ThingsDoneCounter;
import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Component
public class GetSomethingWasDoneCounterValueQueryHandler
        implements QueryHandler<Long, GetSomethingWasDoneCounterValueQuery> {

    private final JPAQueryFactory jpaQueryFactory;
    StudentRepository studentRepository;

    private final EntityManager entityManager;

    @Autowired
    public GetSomethingWasDoneCounterValueQueryHandler(
            StudentRepository studentRepository, EntityManager entityManager) {
        this.studentRepository = studentRepository;
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

        Optional<Student> byId = studentRepository.findById(Long.valueOf("1"));
        System.out.println("byId = " + byId);
        return new ThingsDoneCounter().value();
    }

    @Override
    public Class<GetSomethingWasDoneCounterValueQuery> listenTo() {
        return GetSomethingWasDoneCounterValueQuery.class;
    }
}
