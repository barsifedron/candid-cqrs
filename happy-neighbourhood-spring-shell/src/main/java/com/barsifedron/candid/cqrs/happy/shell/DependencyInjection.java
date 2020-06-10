package com.barsifedron.candid.cqrs.happy.shell;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.happy.domain.EmailRepository;
import com.barsifedron.candid.cqrs.happy.domain.ItemsRepository;
import com.barsifedron.candid.cqrs.happy.domain.LoanRepository;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import com.barsifedron.candid.cqrs.happy.infrastructure.HibernateEmailRepository;
import com.barsifedron.candid.cqrs.happy.infrastructure.HibernateItemRepository;
import com.barsifedron.candid.cqrs.happy.infrastructure.HibernateLoanRepository;
import com.barsifedron.candid.cqrs.happy.infrastructure.HibernateMemberRepository;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command.CommandBusFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

/**
 * A module helping with dependency injection
 */
@Configuration
public class DependencyInjection {

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

    @Bean
    public CommandBus commandBus(CommandBusFactory factory) {
        return factory.simpleBus();
    }

}
