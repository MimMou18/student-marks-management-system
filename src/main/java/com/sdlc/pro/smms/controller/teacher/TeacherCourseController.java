package com.sdlc.pro.smms.controller.teacher;

import com.sdlc.pro.smms.dto.*;
import com.sdlc.pro.smms.service.CourseOfferingService;
import com.sdlc.pro.smms.service.EnrollmentService;
import com.sdlc.pro.smms.util.AuthUtils;
import com.sdlc.pro.smms.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static com.sdlc.pro.smms.util.Constants.ERROR_MESSAGE;
import static com.sdlc.pro.smms.util.Constants.SUCCESS_MESSAGE;

@Slf4j
@Controller
@RequestMapping("/teacher/courses")
@RequiredArgsConstructor
public class TeacherCourseController {
    private final CourseOfferingService courseOfferingService;
    private final EnrollmentService enrollmentService;

    @GetMapping
    public String coursePage() {
        return "teacher/course-list";
    }

    @ResponseBody
    @PostMapping("/datatable")
    public DataTableResponse<OfferedCourseResponse> fetchPageableOfferedCourse(@RequestBody DatatableRequest request) {
        Map<String, String> fieldToDbColumnMap = Map.of(
                "courseCode", "course.code",
                "courseTitle", "course.title",
                "courseTeacher", "teacher.name"
        );

        Integer teacherId = AuthUtils.loggedInUserId();

        Page<OfferedCourseResponse> page = this.courseOfferingService.offeredCourseResponsePageForTeacher(
                teacherId,
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

    @GetMapping("/details/{course_offer_id}")
    public String courseDetails(@PathVariable("course_offer_id") Integer courseOfferId, Model model) {
        Integer teacherId = AuthUtils.loggedInUserId();
        CourseDetails details = this.courseOfferingService.getCourseOfferingByIdAndTeacherEmail(courseOfferId, teacherId);
        model.addAttribute("course_details", details);
        return "teacher/course-details";
    }

    @GetMapping("/marks/{enrollment_id}")
    public String marksPage(@PathVariable("enrollment_id") Long enrollmentId, Model model) {
        Integer teacherId = AuthUtils.loggedInUserId();
        CTMarksInfo ctMarksInfo = this.enrollmentService.getCtMarksInfo(teacherId, enrollmentId);
        model.addAttribute("ctMarksInfo", ctMarksInfo);
        return "teacher/course-marks";
    }

    @PostMapping("/marks/update")
    public String marksUpdate(@Validated @ModelAttribute("ctMarksInfo") CTMarksInfo ctMarksInfo, BindingResult bindingResult, RedirectAttributes attributes) {
        Integer teacherId = AuthUtils.loggedInUserId();

        try {
            this.enrollmentService.updateCtMarks(teacherId, ctMarksInfo);
            attributes.addFlashAttribute(SUCCESS_MESSAGE, "Marks update successfully!");
        } catch (Exception ex) {
            log.error("Failed to update marks, Ex: {}", ex.getMessage());
            attributes.addFlashAttribute(ERROR_MESSAGE, "Failed to update marks!");
        }

        return "redirect:/teacher/courses/marks/" + ctMarksInfo.getEnrollmentId();
    }


}
