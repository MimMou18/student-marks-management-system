package com.sdlc.pro.smms.repository;

import com.sdlc.pro.smms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    boolean existsByCode(String code);

    Optional<Course> findByCode(String code);

    @Query("select c.code from Course c")
    List<String> findAllCourseCode();
}
