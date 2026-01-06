package com.sdlc.pro.smms.controller.admin;

import com.sdlc.pro.smms.dto.DataTableResponse;
import com.sdlc.pro.smms.dto.DatatableRequest;
import com.sdlc.pro.smms.dto.OfferedCourseRequest;
import com.sdlc.pro.smms.dto.OfferedCourseResponse;
import com.sdlc.pro.smms.enums.Semester;
import com.sdlc.pro.smms.exception.DuplicateFieldException;
import com.sdlc.pro.smms.exception.ResourceNotFoundException;
import com.sdlc.pro.smms.service.CourseOfferingService;
import com.sdlc.pro.smms.service.CourseService;
import com.sdlc.pro.smms.service.TeacherService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static com.sdlc.pro.smms.util.Constants.OPERATION_TYPE;
import static com.sdlc.pro.smms.util.Constants.SUCCESS_MESSAGE;

@Controller
@RequestMapping("/admin/course-offering")
@AllArgsConstructor
public class CourseOfferingController {
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final CourseOfferingService courseOfferingService;

    @GetMapping
    public String courseOfferingPage() {
        return "admin/course-offering-list";
    }

    @GetMapping("/create")
    public String addCourseOffer(Model model) {
        if (!model.containsAttribute("offered_course")) {
            model.addAttribute("offered_course", new OfferedCourseRequest());
        }

        model.addAttribute("course_codes", this.courseService.getAllCourseCode()); // don't fetch all entity at once
        model.addAttribute("course_teachers", this.teacherService.getAllTeacherForCourseOffering());
        model.addAttribute("semesters", Semester.values());
        model.addAttribute(OPERATION_TYPE, "Add");
        return "admin/course-offering-form";
    }

    @PostMapping("/create")
    public String addOfferedCourse(@ModelAttribute("offered_course") OfferedCourseRequest offeredCourseRequest, BindingResult bindingResult, RedirectAttributes attributes) {
        System.out.println(offeredCourseRequest);

        attributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX.concat("offered_course"), bindingResult);
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("offered_course", offeredCourseRequest);
            return "redirect:/admin/course-offering/create";
        }

        try {
            this.courseOfferingService.addOfferedCourse(offeredCourseRequest);
            attributes.addFlashAttribute(SUCCESS_MESSAGE, "New offered course added successfully!");
        } catch (DuplicateFieldException | ResourceNotFoundException ex) {
            attributes.addFlashAttribute("offered_course", offeredCourseRequest);
            bindingResult.addError(new ObjectError("teacher", ex.getMessage()));
        }

        return "redirect:/admin/course-offering/create";
    }

    @ResponseBody
    @PostMapping("/datatable")
    public DataTableResponse<OfferedCourseResponse> fetchPageableOfferedCourse(@RequestBody DatatableRequest request) {
        Map<String, String> fieldToDbColumnMap = Map.of(
                "courseCode", "course.code",
                "courseTitle", "course.title",
                "courseTeacher", "teacher.name"
        );

        Page<OfferedCourseResponse> page = this.courseOfferingService.offeredCourseResponsePage(
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

}
