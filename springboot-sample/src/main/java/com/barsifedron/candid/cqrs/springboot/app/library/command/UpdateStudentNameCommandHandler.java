package com.barsifedron.candid.cqrs.springboot.app.library.command;

import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.QStudent;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.Student;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.StudentRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

public class UpdateStudentNameCommandHandler implements CommandHandler<NoResult, UpdateStudentNameCommand> {


    final StudentRepository studentRepository;
    final EntityManager entityManager;

    @Inject
    public UpdateStudentNameCommandHandler(
            StudentRepository studentRepository,
            EntityManager entityManager) {
        this.studentRepository = studentRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public CommandResponse<NoResult> handle(UpdateStudentNameCommand command) {

        List<Student> fetch = new JPAQueryFactory(entityManager).select(QStudent.student)
                .from(QStudent.student)
                .fetch();

        System.out.println("fetch = " + fetch);

        Student student = new JPAQueryFactory(entityManager).select(QStudent.student)
                .from(QStudent.student)
                .where(QStudent.student.id.eq(Long.valueOf(1)))
                .fetchOne();
        student.setName("tata");
//        studentRepository.save(student);
        return CommandResponse.empty();
    }

    @Override
    public Class<UpdateStudentNameCommand> listenTo() {
        return UpdateStudentNameCommand.class;
    }
}
