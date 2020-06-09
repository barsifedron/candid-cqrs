package com.barsifedron.candid.cqrs.springboot;

import com.barsifedron.candid.cqrs.happy.domain.EmailRepository;
import com.barsifedron.candid.cqrs.happy.domain.ItemsRepository;
import com.barsifedron.candid.cqrs.happy.domain.LoanRepository;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import com.barsifedron.candid.cqrs.happy.infrastructure.HibernateEmailRepository;
import com.barsifedron.candid.cqrs.happy.infrastructure.HibernateItemRepository;
import com.barsifedron.candid.cqrs.happy.infrastructure.HibernateLoanRepository;
import com.barsifedron.candid.cqrs.springboot.app.library.domain.MyStudentRepository;
import com.barsifedron.candid.cqrs.happy.infrastructure.HibernateMemberRepository;
import com.barsifedron.candid.cqrs.springboot.app.library.infrastructure.HibernateStudentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class DependencyInjection {

    @Bean
    public MyStudentRepository myRepository(EntityManager engine) {
        return new HibernateStudentRepository(engine);
    }

    @Bean
    public MembersRepository membersRepository(EntityManager entityManager) {
        return new HibernateMemberRepository(entityManager);
    }

    @Bean
    public ItemsRepository itemsRepository(EntityManager entityManager) {
        return new HibernateItemRepository(entityManager);
    }

    @Bean
    public LoanRepository loanRepository(EntityManager entityManager) {
        return new HibernateLoanRepository(entityManager);
    }

    @Bean
    public EmailRepository emailRepository(EntityManager entityManager) {
        return new HibernateEmailRepository(entityManager);
    }

}
