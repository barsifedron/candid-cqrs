package com.barsifedron.candid.cqrs.springboot.app.library.command;

import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.Student;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.StudentRepository;
import com.barsifedron.candid.cqrs.springboot.app.library.domainevent.SomethingWasDoneEvent;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

@Component
public class PleaseDoSomethingCommandHandler implements CommandHandler<String, PleaseDoSomethingCommand> {

    StudentRepository studentRepository;

    @Inject
    public PleaseDoSomethingCommandHandler(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public CommandResponse<String> handle(PleaseDoSomethingCommand command) {

        Student student =    Student
                .builder()
                .id(Long.valueOf("1"))
                .name("toto")
                .build();
        studentRepository.save(student);
        Optional<Student> byId = studentRepository.findById(Long.valueOf("1"));
        System.out.println("byId = " + byId);

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
