package com.barsifedron.candid.cqrs.springboot.app.library.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {


    Student findById(Integer id);
}