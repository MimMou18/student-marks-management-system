package com.sdlc.pro.smms.service;

import com.sdlc.pro.smms.dto.CTMark;
import com.sdlc.pro.smms.dto.CTMarksInfo;
import com.sdlc.pro.smms.dto.EnrolledCourse;
import com.sdlc.pro.smms.dto.MarksResponse;
import com.sdlc.pro.smms.entity.ClassTestMark;
import com.sdlc.pro.smms.entity.CourseOffering;
import com.sdlc.pro.smms.entity.Enrollment;
import com.sdlc.pro.smms.entity.Student;
import com.sdlc.pro.smms.exception.InvalidEnrollmentException;
import com.sdlc.pro.smms.exception.ResourceNotFoundException;
import com.sdlc.pro.smms.repository.CourseOfferingRepository;
import com.sdlc.pro.smms.repository.EnrollmentRepository;
import com.sdlc.pro.smms.repository.StudentRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseOfferingRepository courseOfferingRepository;

    public void enrollStudentInfoOfferCourse(Integer studentId, Integer offeredCourseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found by"));

        CourseOffering offering = courseOfferingRepository.findById(offeredCourseId)
                .orElseThrow(() -> new ResourceNotFoundException("Offered course not found"));

        if (enrollmentRepository.existsByStudentAndCourseOffering(student, offering)) {
            throw new InvalidEnrollmentException("Already enrolled");
        }

        if (student.getSemester() != offering.getSemester()) {
            throw new InvalidEnrollmentException("This course is not available for your semester");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourseOffering(offering);

        enrollment.addClassTestMark(new ClassTestMark(enrollment, 1, 0.0));
        enrollment.addClassTestMark(new ClassTestMark(enrollment, 2, 0.0));
        enrollment.addClassTestMark(new ClassTestMark(enrollment, 3, 0.0));

        try {
            this.enrollmentRepository.save(enrollment);
        } catch (Exception ex) {
            log.error("Enrollment exception, Ex: {}", ex.getMessage());
            throw new PersistenceException("Unexpected exception occurred while enrolling");
        }
    }

    @Transactional(readOnly = true)
    public Page<EnrolledCourse> enrolledCoursePage(Integer studentId, String search, Pageable pageable) {
        return this.enrollmentRepository.findEnrolledCourses(studentId, search, pageable);
    }

    @Transactional(readOnly = true)
    public MarksResponse enrolledCourseMarks(Integer studentId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentId(enrollmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrolment not found!"));

        CourseOffering offering = enrollment.getCourseOffering();
        List<CTMark> ctMarks = extractCtMarks(enrollment);

        MarksResponse marksResponse = new MarksResponse();
        marksResponse.setCourseCode(offering.getCourse().getCode());
        marksResponse.setCourseTitle(offering.getCourse().getTitle());
        marksResponse.setCtMarks(ctMarks);

        return marksResponse;
    }

    @Transactional(readOnly = true)
    public CTMarksInfo getCtMarksInfo(Integer teacherId, Long enrollmentId) {
        Enrollment enrollment = this.enrollmentRepository.findByIdAndTeacherId(enrollmentId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found!"));

        List<CTMark> ctMarks = extractCtMarks(enrollment);

        CTMarksInfo info = new CTMarksInfo();
        info.setEnrollmentId(enrollment.getId());
        info.setMarks(ctMarks);

        return info;
    }

    private List<CTMark> extractCtMarks(Enrollment enrollment) {
        return enrollment.getClassTestMarks()
                .stream()
                .map(c -> new CTMark(c.getTestNo(), c.getMarks()))
                .toList();
    }

    @Transactional
    public void updateCtMarks(Integer teacherId, CTMarksInfo info) {
        Long enrollmentId = info.getEnrollmentId();
        Enrollment enrollment = this.enrollmentRepository.findByIdAndTeacherId(enrollmentId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found!"));

        List<CTMark> ctMarks = info.getMarks();
        for (var ct : enrollment.getClassTestMarks()) {
            var updateValue = ctMarks.stream()
                    .filter(c -> Objects.equals(c.getTestNo(), ct.getTestNo()))
                    .findFirst();

            updateValue.ifPresent(ctMark -> ct.setMarks(ctMark.getMarks()));
        }

        System.out.println(enrollment);
    }
}
