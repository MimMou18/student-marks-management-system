package com.sdlc.pro.smms.service;

import com.sdlc.pro.smms.dto.CourseTeacherInfo;
import com.sdlc.pro.smms.dto.TeacherRequest;
import com.sdlc.pro.smms.dto.TeacherResponse;
import com.sdlc.pro.smms.entity.Teacher;
import com.sdlc.pro.smms.enums.Role;
import com.sdlc.pro.smms.event.AppUserCreationEvent;
import com.sdlc.pro.smms.exception.DuplicateFieldException;
import com.sdlc.pro.smms.exception.ResourceNotFoundException;
import com.sdlc.pro.smms.repository.TeacherRepository;
import com.sdlc.pro.smms.util.Constants;
import com.sdlc.pro.smms.util.ExceptionTranslator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.data.domain.ExampleMatcher.StringMatcher;
import static org.springframework.data.domain.ExampleMatcher.matchingAny;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final ModelMapper mapper;
    private final TeacherRepository teacherRepository;

    private final ApplicationEventPublisher eventPublisher;

    public void saveTeacher(TeacherRequest teacherRequest) {
        if (this.teacherRepository.existsByEmail(teacherRequest.getEmail())) {
            throw new DuplicateFieldException("Email already exist!");
        }

        teacherRequest.setId(null);
        Teacher teacher = this.mapper.map(teacherRequest, Teacher.class);
        String resetPasswordSecret = UUID.randomUUID().toString();
        teacher.setResetPasswordSecret(resetPasswordSecret);
        teacher.setRole(Role.TEACHER);

        // set default password
        teacher.setPassword(Constants.DEFAULT_PASSWORD);

        try {
            this.teacherRepository.save(teacher);
        } catch (DataIntegrityViolationException ex) {
            throw ExceptionTranslator.handleDuplicateConstraint(ex);
        }

        this.eventPublisher.publishEvent(new AppUserCreationEvent(teacher));
    }

    public void updateTeacher(TeacherRequest teacherRequest) {
        Integer teacherId = teacherRequest.getId();
        Teacher teacher = this.teacherRepository
                .findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found by id: " + teacherId));

        if (this.teacherRepository.existsByEmailAndIdNot(teacherRequest.getEmail(), teacherId)) {
            throw new DuplicateFieldException("Email already exist!");
        }

        this.mapper.map(teacherRequest, teacher);
        try {
            this.teacherRepository.save(teacher);
        } catch (DataIntegrityViolationException ex) {
            throw ExceptionTranslator.handleDuplicateConstraint(ex);
        }
    }

    @Transactional(readOnly = true)
    public TeacherResponse teacherById(Integer id) {
        return teacherRepository.findById(id)
                .map(c -> mapper.map(c, TeacherResponse.class))
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found by teacher id=" + id));
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponse> teacherResponsePage(String search, Pageable pageable) {
        Teacher teacher = new Teacher();
        teacher.setName(search);
        teacher.setEmail(search);
        teacher.setDesignation(search);

        Example<Teacher> example = Example.of(
                teacher,
                matchingAny()
                        .withIgnoreCase("name", "email", "designation")
                        .withStringMatcher(StringMatcher.CONTAINING)
        );

        return teacherRepository.findAll(example, pageable)
                .map(t -> mapper.map(t, TeacherResponse.class));
    }

    @Transactional(readOnly = true)
    public List<CourseTeacherInfo> getAllTeacherForCourseOffering() {
        return this.teacherRepository.findAll()
                .stream()
                .map(t-> this.mapper.map(t, CourseTeacherInfo.class))
                .toList();
    }
}
