package com.sdlc.pro.smms.controller.admin;

import com.sdlc.pro.smms.dto.*;
import com.sdlc.pro.smms.enums.Semester;
import com.sdlc.pro.smms.exception.DuplicateFieldException;
import com.sdlc.pro.smms.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class StudentController {
    private final ModelMapper mapper;
    private final StudentService studentService;

    @GetMapping
    public String studentPage() {
        return "admin/student-list";
    }

    @GetMapping("/create")
    public String addTeacherForm(Model model) {
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new StudentRequest());
        }
        model.addAttribute("semesters", Semester.values());
        model.addAttribute(OPERATION_TYPE, "Add");
        return "admin/student-form";
    }

    @GetMapping("/update/{student_id}")
    public String editCourseForm(@PathVariable("student_id") Integer teacherId, Model model) {
        if (!model.containsAttribute("student")) {
            StudentResponse student = studentService.studentById(teacherId);
            model.addAttribute("student", mapper.map(student, StudentRequest.class));
        }
        model.addAttribute("semesters", Semester.values());
        model.addAttribute(OPERATION_TYPE, "Update");
        return "admin/student-form";
    }

    @PostMapping("/create")
    public String addStudent(@Validated @ModelAttribute("student") StudentRequest student, BindingResult bindingResult, RedirectAttributes attributes) {
        attributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX.concat("student"), bindingResult);
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("student", student);
            return "redirect:/admin/students/create";
        }

        try {
            this.studentService.saveStudent(student);
            attributes.addFlashAttribute(SUCCESS_MESSAGE, "New student added successfully!");
        } catch (DuplicateFieldException ex) {
            attributes.addFlashAttribute("student", student);
            bindingResult.addError(new ObjectError("student", ex.getMessage()));
        }

        return "redirect:/admin/students/create";
    }

    @PostMapping( "/update")
    public String updateStudent(@Validated @ModelAttribute("student") StudentRequest student, BindingResult bindingResult, RedirectAttributes attributes) {
        attributes.addFlashAttribute("student", student);
        attributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX.concat("student"), bindingResult);
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/students/update/" + student.getId();
        }

        try {
            this.studentService.updateStudent(student);
            attributes.addFlashAttribute(SUCCESS_MESSAGE, "Teacher's info updated successfully!");
        } catch (DuplicateFieldException ex) {
            bindingResult.addError(new ObjectError("student", "Another student is found with same email or roll"));
            return "admin/student-form";
        }

        return "redirect:/admin/students/update/" + student.getId();
    }

    @ResponseBody
    @PostMapping("/datatable")
    public DataTableResponse<StudentResponse> fetchPageableStudents(@RequestBody DatatableRequest request) {
        Page<StudentResponse> page = this.studentService.studentResponsePage(
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
