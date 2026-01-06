package com.sdlc.pro.smms.service;

import com.sdlc.pro.smms.dto.*;
import com.sdlc.pro.smms.entity.Course;
import com.sdlc.pro.smms.entity.CourseOffering;
import com.sdlc.pro.smms.entity.Enrollment;
import com.sdlc.pro.smms.entity.Teacher;
import com.sdlc.pro.smms.enums.Semester;
import com.sdlc.pro.smms.exception.ResourceNotFoundException;
import com.sdlc.pro.smms.repository.CourseOfferingRepository;
import com.sdlc.pro.smms.repository.CourseRepository;
import com.sdlc.pro.smms.repository.TeacherRepository;
import com.sdlc.pro.smms.util.ExceptionTranslator;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseOfferingService {
    private final ModelMapper mapper;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseOfferingRepository courseOfferingRepository;

    /*
    private final TypeMap<CourseOffering, OfferedCourseResponse> courseOfferingToOfferedCourseResponseTypeMap;

    public CourseOfferingService(ModelMapper mapper, CourseRepository courseRepository,
                                 TeacherRepository teacherRepository,
                                 CourseOfferingRepository courseOfferingRepository) {
        this.mapper = mapper;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.courseOfferingRepository = courseOfferingRepository;

        this.courseOfferingToOfferedCourseResponseTypeMap = this.mapper.createTypeMap(CourseOffering.class, OfferedCourseResponse.class);
        this.courseOfferingToOfferedCourseResponseTypeMap.addMappings(exp -> {
            exp.map(src -> src.getCourse().getCode(), OfferedCourseResponse::setCourseCode);
            exp.map(src -> src.getCourse().getTitle(), OfferedCourseResponse::setCourseTitle);
            exp.map(src-> src.getTeacher().getName(), OfferedCourseResponse::setCourseTeacher);
        });
    }
     */


    public void addOfferedCourse(OfferedCourseRequest offeredCourseRequest) {
        String courseCode = offeredCourseRequest.getCourseCode();
        String teacherEmail = offeredCourseRequest.getTeacherEmail();

        Course course = this.courseRepository.findByCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found by course code=" + courseCode));

        Teacher teacher = this.teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found by email=" + teacherEmail));

        Semester semester = offeredCourseRequest.getSemester();
        Integer year = offeredCourseRequest.getYear();

        CourseOffering courseOffering = new CourseOffering();
        courseOffering.setCourse(course);
        courseOffering.setTeacher(teacher);
        courseOffering.setSemester(semester);
        courseOffering.setYear(year);

        try {
            this.courseOfferingRepository.save(courseOffering);
        } catch (DataIntegrityViolationException ex) {
            throw ExceptionTranslator.handleDuplicateConstraint(ex);
        }
    }

    @Transactional(readOnly = true)
    public Page<OfferedCourseResponse> offeredCourseResponsePage(String search, Pageable pageable) {
        /*
        CourseOffering courseOffering = new CourseOffering();
        courseOffering.getCourse().setCode(search);
        courseOffering.getCourse().setTitle(search);
        courseOffering.getTeacher().setName();
        Example<CourseOffering> example = Example.of(
                courseOffering,
                matchingAny()
                        .withIgnoreNullValues()
                        .withIgnoreCase("course.code", "course.title", "teacher.name").withStringMatcher(StringMatcher.CONTAINING)
        );

        return this.courseOfferingRepository.findAll(example, pageable)
                .map(this.courseOfferingToOfferedCourseResponseTypeMap::map);

         */

        return this.courseOfferingRepository.findPageableOfferedCourseResponse(search, pageable);
    }


    @Transactional(readOnly = true)
    public Page<OfferedCourseResponse> offeredCourseResponsePageForTeacher(Integer teacherId, String search, Pageable pageable) {
        return this.courseOfferingRepository.findPageableOfferedCourseResponseForTeacher(teacherId, search, pageable);
    }

    @Transactional(readOnly = true)
    public CourseDetails getCourseOfferingByIdAndTeacherEmail(Integer id, Integer teacherId) {
        CourseOffering offering = this.courseOfferingRepository.findCourseOfferingByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        List<CourseDetails.StudentInfo> students = offering.getEnrollments()
                .stream()
                .map(CourseOfferingService::fromEnrollment)
                .toList();

        Course course = offering.getCourse();
        CourseDetails details = new CourseDetails();
        details.setCode(course.getCode());
        details.setTitle(course.getTitle());
        details.setSemester(offering.getSemester());
        details.setYear(offering.getYear());
        details.setStudents(students);

        return details;
    }

    private static CourseDetails.StudentInfo fromEnrollment(Enrollment enrollment) {
        var stu = enrollment.getStudent();

        List<CTMark> ctMarks = enrollment.getClassTestMarks()
                .stream()
                .map(c -> new CTMark(c.getTestNo(), c.getMarks()))
                .toList();

        var info = new CourseDetails.StudentInfo();
        info.setEnrollmentId(enrollment.getId());
        info.setRoll(stu.getRoll());
        info.setName(stu.getName());
        info.setCtMarks(ctMarks);
        return info;
    }

    @Transactional(readOnly = true)
    public List<AvailableCourse> currentSemesterAvailableCourses(Integer studentId, Semester semester) {
        return this.courseOfferingRepository.findOfferedCoursesForCurrentSemester(studentId, semester);
    }

}
