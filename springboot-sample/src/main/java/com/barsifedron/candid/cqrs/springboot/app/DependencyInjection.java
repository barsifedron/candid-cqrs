package com.barsifedron.candid.cqrs.springboot.app;

import com.barsifedron.candid.cqrs.springboot.app.library.domain.MyStudentRepository;
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

}
