package com.sdlc.pro.smms.controller.student;

import com.sdlc.pro.smms.dto.*;
import com.sdlc.pro.smms.enums.Semester;
import com.sdlc.pro.smms.service.CourseOfferingService;
import com.sdlc.pro.smms.service.EnrollmentService;
import com.sdlc.pro.smms.util.AuthUtils;
import com.sdlc.pro.smms.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student/courses")
@RequiredArgsConstructor
public class StudentCourseController {
    private final CourseOfferingService courseOfferingService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/available")
    public String availableCourse(Model model) {
        Integer studentId = AuthUtils.loggedInUserId();
        Semester semester = AuthUtils.getCurrentSemester();

        List<AvailableCourse> availableCourses = this.courseOfferingService.currentSemesterAvailableCourses(
                studentId,
                semester
        );

        model.addAttribute("available_courses", availableCourses);
        return "student/available-courses";
    }

    @PostMapping("/enroll")
    public String enroll(@RequestParam("offeredCourseId") Integer offeredCourseId, RedirectAttributes attributes) {
        Integer studentId = AuthUtils.loggedInUserId();
        try {
            this.enrollmentService.enrollStudentInfoOfferCourse(studentId, offeredCourseId);
            attributes.addFlashAttribute(Constants.SUCCESS_MESSAGE, "Congratulations, You have successfully enrolled!");
        } catch (Exception ex) {
            attributes.addFlashAttribute(Constants.ERROR_MESSAGE, ex.getMessage());
        }

        return "redirect:/student/courses/available";
    }

    @GetMapping("/enrolled")
    public String enrolledCoursePage() {
        return "student/enrolled-courses";
    }

    @ResponseBody
    @PostMapping("/enrolled/datatable")
    public DataTableResponse<EnrolledCourse> fetchEnrolledCourses(@RequestBody DatatableRequest request) {
        Integer studentId = AuthUtils.loggedInUserId();

        Map<String, String> fieldToDbColumnMap = Map.of(
                "enrollmentId", "id",
                "courseCode", "courseOffering.course.code",
                "courseTitle", "courseOffering.course.title",
                "teacherName", "e.courseOffering.teacher.name",
                "semester", "e.courseOffering.semester",
                "year", "e.courseOffering.year"
        );

        Page<EnrolledCourse> page = this.enrollmentService.enrolledCoursePage(
                studentId,
                request.searchValue(),
                request.toPageable(fieldToDbColumnMap)
        );

        return new DataTableResponse<>(
                request.getDraw(),
                page.getTotalElements(),
                page.getTotalElements(),
                page.getContent()
        );
    }

    @GetMapping("/marks/{enrollment_id}")
    public String enrolledCourseMarks(@PathVariable("enrollment_id") Long enrollmentId, Model model) {
        Integer studentId = AuthUtils.loggedInUserId();
        MarksResponse marksResponse = this.enrollmentService.enrolledCourseMarks(studentId, enrollmentId);
        model.addAttribute("marks_response", marksResponse);
        return "student/course-marks";
    }

}
