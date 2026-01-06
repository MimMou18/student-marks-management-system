package com.sdlc.pro.smms.repository;

import com.sdlc.pro.smms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    boolean existsById(Integer id);

    boolean existsByRoll(String roll);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    boolean existsByRollAndIdNot(String roll, Integer id);

    Optional<Student> findByEmail(String email);
}
