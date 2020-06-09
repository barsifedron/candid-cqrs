package com.barsifedron.candid.cqrs.springboot.app.library.infrastructure;

import com.barsifedron.candid.cqrs.springboot.app.library.domain.MyStudentRepository;
import com.barsifedron.candid.cqrs.happy.domain.Student;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;


public class HibernateStudentRepository implements MyStudentRepository {

    private final EntityManager entityManager;

    @Inject
    public HibernateStudentRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
//    @Transactional
    public void save(Student student) {

        entityManager.persist(student);

    }
}
