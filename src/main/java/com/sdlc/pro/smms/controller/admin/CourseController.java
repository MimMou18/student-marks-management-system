package com.sdlc.pro.smms.controller.admin;

import com.sdlc.pro.smms.dto.CourseResponse;
import com.sdlc.pro.smms.dto.CourseRequest;
import com.sdlc.pro.smms.dto.DataTableResponse;
import com.sdlc.pro.smms.dto.DatatableRequest;
import com.sdlc.pro.smms.exception.DuplicateFieldException;
import com.sdlc.pro.smms.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.sdlc.pro.smms.util.Constants.OPERATION_TYPE;
import static com.sdlc.pro.smms.util.Constants.SUCCESS_MESSAGE;

@Slf4j
@Controller
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public String coursePage() {
        return "admin/course-list";
    }

    @GetMapping("/create")
    public String addCourseForm(Model model) {
        model.addAttribute("course", new CourseRequest());
        model.addAttribute(OPERATION_TYPE, "Add");
        return "admin/course-form";
    }

    @GetMapping("/update/{course_id}")
    public String editCourseForm(@PathVariable("course_id") Integer courseId, Model model) {
        CourseResponse course = courseService.courseById(courseId);
        model.addAttribute("course", new CourseRequest(courseId, course.getCode(), course.getTitle()));
        model.addAttribute(OPERATION_TYPE, "Update");
        return "admin/course-form";
    }

    @PostMapping({"/create", "/update"})
    public String addCourse(@Validated @ModelAttribute("course") CourseRequest course, BindingResult bindingResult,
                            Model model, RedirectAttributes attributes) {
        model.addAttribute("course", course);
        if (bindingResult.hasErrors()) {
            return "admin/course-form";
        }

        try {
            this.courseService.saveCourse(course);
        } catch (DuplicateFieldException ex) {
            bindingResult.addError(new ObjectError("course", "Another course is found with same course code"));
            return "admin/course-form";
        }

        Integer courseId = course.getId();
        attributes.addFlashAttribute(SUCCESS_MESSAGE, courseId == null ? "New course added successfully!" : "Course updated successfully!");
        return courseId == null ? "redirect:/admin/courses/create" : "redirect:/admin/courses/update/" + courseId;
    }

    @ResponseBody
    @PostMapping("/datatable")
    public DataTableResponse<CourseResponse> fetchPageableCourses(@RequestBody DatatableRequest request) {
        Page<CourseResponse> page = courseService.courseResponsePage(
                request.searchValue(),
                request.toPageable()
        );

        return new DataTableResponse<>(
                request.getDraw(),
                page.getTotalElements(),
                page.getTotalElements(),
                page.getContent()
        );
    }
}
