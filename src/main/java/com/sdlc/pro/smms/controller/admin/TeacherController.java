package com.sdlc.pro.smms.controller.admin;

import com.sdlc.pro.smms.dto.DataTableResponse;
import com.sdlc.pro.smms.dto.DatatableRequest;
import com.sdlc.pro.smms.dto.TeacherRequest;
import com.sdlc.pro.smms.dto.TeacherResponse;
import com.sdlc.pro.smms.exception.DuplicateFieldException;
import com.sdlc.pro.smms.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
@RequestMapping({"/admin/teachers"})
@RequiredArgsConstructor
public class TeacherController {
    private final ModelMapper mapper;
    private final TeacherService teacherService;

    @GetMapping
    public String teacherPage() {
        return "admin/teacher-list";
    }

    @GetMapping("/create")
    public String addTeacherForm(Model model) {
        if (!model.containsAttribute("teacher")) {
            model.addAttribute("teacher", new TeacherRequest());
        }
        model.addAttribute(OPERATION_TYPE, "Add");
        return "admin/teacher-form";
    }

    @GetMapping("/update/{teacher_id}")
    public String editCourseForm(@PathVariable("teacher_id") Integer teacherId, Model model) {
        if (!model.containsAttribute("teacher")) {
            TeacherResponse teacher = teacherService.teacherById(teacherId);
            model.addAttribute("teacher", mapper.map(teacher, TeacherRequest.class));
        }
        model.addAttribute(OPERATION_TYPE, "Update");
        return "admin/teacher-form";
    }

    @PostMapping("/create")
    public String addTeacher(@Validated @ModelAttribute("teacher") TeacherRequest teacher, BindingResult bindingResult, RedirectAttributes attributes) {
        attributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX.concat("teacher"), bindingResult);
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("teacher", teacher);
            return "redirect:/admin/teachers/create";
        }

        try {
            this.teacherService.saveTeacher(teacher);
            attributes.addFlashAttribute(SUCCESS_MESSAGE, "New teacher added successfully!");
        } catch (DuplicateFieldException ex) {
            attributes.addFlashAttribute("teacher", teacher);
            bindingResult.addError(new ObjectError("teacher", ex.getMessage()));
        }

        return "redirect:/admin/teachers/create";
    }

    @PostMapping("/update")
    public String updateTeacher(@Validated @ModelAttribute("teacher") TeacherRequest teacher, BindingResult bindingResult, RedirectAttributes attributes) {
        attributes.addFlashAttribute("teacher", teacher);
        attributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX.concat("teacher"), bindingResult);
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/teachers/update/" + teacher.getId();
        }

        try {
            this.teacherService.updateTeacher(teacher);
            attributes.addFlashAttribute(SUCCESS_MESSAGE, "Teacher's info updated successfully!");
        } catch (DuplicateFieldException ex) {
            bindingResult.addError(new ObjectError("teacher", ex.getMessage()));
        }

        return "redirect:/admin/teachers/update/" + teacher.getId();
    }

    @ResponseBody
    @PostMapping("/datatable")
    public DataTableResponse<TeacherResponse> fetchPageableTeachers(@RequestBody DatatableRequest request) {
        Page<TeacherResponse> page = teacherService.teacherResponsePage(
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
