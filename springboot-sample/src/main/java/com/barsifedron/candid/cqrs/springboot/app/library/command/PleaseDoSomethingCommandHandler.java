package com.barsifedron.candid.cqrs.springboot.app.library.command;

import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.MyStudentRepository;
import com.barsifedron.candid.cqrs.happy.domain.Student;
import com.barsifedron.candid.cqrs.springboot.app.library.domainevent.SomethingWasDoneEvent;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

public class PleaseDoSomethingCommandHandler implements CommandHandler<String, PleaseDoSomethingCommand> {

//    private final EntityManager entityManager;
    private final MyStudentRepository studentRepository;
    private final MembersRepository membersRepository;

    @Inject
    public PleaseDoSomethingCommandHandler(

            MyStudentRepository studentRepository,
            MembersRepository membersRepository) {
//        this.entityManager = entityManager;
        this.studentRepository = studentRepository;
        this.membersRepository = membersRepository;
    }

    @Override
//    @Transactional
    public CommandResponse<String> handle(PleaseDoSomethingCommand command) {

        Student student = Student
                .builder()
                .id(Long.valueOf("1"))
                .name("toto")
                .build();

        //        entityManager.persist(student);
//        studentRepository.save(student);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Member member = Member
                .builder()
                .memberId(new MemberId("youpiyoupi"))
                .email("email@enmail.com")
                .surname("sdd")
                .firstname("ddd")
                .build();

        membersRepository.add(member);


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
