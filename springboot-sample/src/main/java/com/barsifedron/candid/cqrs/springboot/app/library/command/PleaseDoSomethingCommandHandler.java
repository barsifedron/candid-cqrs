package com.barsifedron.candid.cqrs.springboot.app.library.command;

import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.MyStudentRepository;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.Student;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.StudentRepository;
import com.barsifedron.candid.cqrs.springboot.app.library.domainevent.SomethingWasDoneEvent;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Optional;

public class PleaseDoSomethingCommandHandler implements CommandHandler<String, PleaseDoSomethingCommand> {

    private final EntityManager entityManager;
    private final MyStudentRepository studentRepository;

    @Inject
    public PleaseDoSomethingCommandHandler(EntityManager entityManager,
            MyStudentRepository studentRepository) {
        this.entityManager = entityManager;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public CommandResponse<String> handle(PleaseDoSomethingCommand command) {

        Student student =    Student
                .builder()
                .id(Long.valueOf("1"))
                .name("toto")
                .build();

//        entityManager.persist(student);
        studentRepository.save(student);



        return CommandResponse
                .empty()
                .withResult("DONE!")
                .withAddedDomainEvents(
                        new SomethingWasDoneEvent(
                                "barsifedron@no-spam.com",
                                666)
                );
    }

    @Override
    public Class<PleaseDoSomethingCommand> listenTo() {
        return PleaseDoSomethingCommand.class;
    }

}
