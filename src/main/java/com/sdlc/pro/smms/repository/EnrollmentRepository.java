package com.sdlc.pro.smms.repository;

import com.sdlc.pro.smms.dto.EnrolledCourse;
import com.sdlc.pro.smms.entity.CourseOffering;
import com.sdlc.pro.smms.entity.Enrollment;
import com.sdlc.pro.smms.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentAndCourseOffering(Student student, CourseOffering courseOffering);

    @Query("""
            select new com.sdlc.pro.smms.dto.EnrolledCourse (
                e.id, \s
                e.courseOffering.course.code, \s
                e.courseOffering.course.title, \s
                e.courseOffering.teacher.name, \s
                e.courseOffering.semester, \s
                e.courseOffering.year
            ) \s
            from Enrollment e where e.student.id = :student_id \s
            and (:search is NULL or :search = '' or
                    lower(e.courseOffering.course.code) like lower(concat('%', :search, '%')) or
                    lower(e.courseOffering.course.title) like lower(concat('%', :search, '%')) or
                    lower(e.courseOffering.teacher.name) like lower(concat('%', :search, '%'))
            )
            """)
    Page<EnrolledCourse> findEnrolledCourses(
            @Param("student_id") Integer studentId,
            @Param("search") String search,
            Pageable pageable
    );


    Optional<Enrollment> findByIdAndStudentId(Long id, Integer studentId);

    @Query("select e from Enrollment e where e.id = ?1 and  e.courseOffering.teacher.id = ?2")
    Optional<Enrollment> findByIdAndTeacherId(Long id, Integer teacherId);
}
