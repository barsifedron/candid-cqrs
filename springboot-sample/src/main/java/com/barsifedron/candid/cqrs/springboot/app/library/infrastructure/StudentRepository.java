package com.barsifedron.candid.cqrs.springboot.app.library.infrastructure;

import com.barsifedron.candid.cqrs.happy.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findById(Integer id);
}
