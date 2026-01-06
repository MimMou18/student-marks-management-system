package com.sdlc.pro.smms.repository;

import com.sdlc.pro.smms.dto.AvailableCourse;
import com.sdlc.pro.smms.dto.EnrolledCourse;
import com.sdlc.pro.smms.dto.OfferedCourseResponse;
import com.sdlc.pro.smms.entity.CourseOffering;
import com.sdlc.pro.smms.enums.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Integer> {

    @Query("""
            select new com.sdlc.pro.smms.dto.OfferedCourseResponse(c.id, c.course.code, c.course.title, c.teacher.name, c.semester, c.year) \s
            from CourseOffering c \s
            where (:search is NULL or :search = '' or
                    lower(c.course.code) like lower(concat('%', :search, '%') ) or
                    lower(c.course.title) LIKE lower(concat('%', :search, '%')) or
                    lower(c.teacher.name) LIKE lower(concat('%', :search, '%')))
            """)
    Page<OfferedCourseResponse> findPageableOfferedCourseResponse(String search, Pageable pageable);


    @Query("""
            select new com.sdlc.pro.smms.dto.OfferedCourseResponse(c.id, c.course.code, c.course.title, c.teacher.name, c.semester, c.year) \s
            from CourseOffering c \s
            where c.teacher.id = :teacher_id \s
            and (:search is NULL or :search = '' or
                    lower(c.course.code) like lower(concat('%', :search, '%')) or
                    lower(c.course.title) like lower(concat('%', :search, '%'))
            )
            """)
    Page<OfferedCourseResponse> findPageableOfferedCourseResponseForTeacher(@Param("teacher_id") Integer teacherId, String search, Pageable pageable);

    @Query("select c from CourseOffering c where c.id=?1 and c.teacher.id = ?2")
    Optional<CourseOffering> findCourseOfferingByIdAndTeacherId(Integer id, Integer teacherId);

    @Query("""
            select new com.sdlc.pro.smms.dto.AvailableCourse(
                c.id, \s
                c.course.code, \s
                c.course.title, \s
                c.teacher.name, \s
                c.semester, \s
                c.year, \s
                case when e.id is not NULL then true else false end, \s
                e.id) \s
            from CourseOffering c left join Enrollment e on e.courseOffering = c and e.student.id = :student_id \s
            where c.semester = :semester \s
            """)
    List<AvailableCourse> findOfferedCoursesForCurrentSemester(
            @Param("student_id") Integer studentId,
            @Param("semester") Semester semester
    );
}
