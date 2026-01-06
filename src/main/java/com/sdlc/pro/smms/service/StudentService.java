package com.sdlc.pro.smms.service;

import com.sdlc.pro.smms.dto.StudentRequest;
import com.sdlc.pro.smms.dto.StudentResponse;
import com.sdlc.pro.smms.entity.Student;
import com.sdlc.pro.smms.enums.Role;
import com.sdlc.pro.smms.event.AppUserCreationEvent;
import com.sdlc.pro.smms.exception.DuplicateFieldException;
import com.sdlc.pro.smms.exception.ResourceNotFoundException;
import com.sdlc.pro.smms.repository.StudentRepository;
import com.sdlc.pro.smms.util.Constants;
import com.sdlc.pro.smms.util.ExceptionTranslator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.data.domain.ExampleMatcher.matchingAny;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final ModelMapper mapper;
    private final StudentRepository studentRepository;

    private final ApplicationEventPublisher eventPublisher;

    public void saveStudent(StudentRequest studentRequest) {
        this.validateBeforeSave(studentRequest);

        studentRequest.setId(null);
        Student student = this.mapper.map(studentRequest, Student.class);
        String resetPasswordSecret = UUID.randomUUID().toString();
        student.setResetPasswordSecret(resetPasswordSecret);
        student.setRole(Role.STUDENT);

        // set default password
        student.setPassword(Constants.DEFAULT_PASSWORD);

        try {
            this.studentRepository.save(student);
        } catch (DataIntegrityViolationException ex) {
            throw ExceptionTranslator.handleDuplicateConstraint(ex);
        }

        this.eventPublisher.publishEvent(new AppUserCreationEvent(student));
    }

    private void validateBeforeSave(StudentRequest studentRequest) {
        if (this.studentRepository.existsByRoll(studentRequest.getRoll())) {
            throw new DuplicateFieldException("Roll already exist!");
        }

        if (this.studentRepository.existsByEmail(studentRequest.getEmail())) {
            throw new DuplicateFieldException("Email already exist!");
        }
    }

    public void updateStudent(StudentRequest studentRequest) {
        Student student = this.validateBeforeUpdate(studentRequest);
        this.mapper.map(studentRequest, student);
        try {
            this.studentRepository.save(student);
        } catch (DataIntegrityViolationException ex) {
            throw ExceptionTranslator.handleDuplicateConstraint(ex);
        }
    }

    private Student validateBeforeUpdate(StudentRequest studentRequest) {
        Integer studentId = studentRequest.getId();
        Student student = this.studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found by id: " + studentId));

        if (this.studentRepository.existsByRollAndIdNot(studentRequest.getRoll(), studentId)) {
            throw new DuplicateFieldException("Roll already exist!");
        }

        if (this.studentRepository.existsByEmailAndIdNot(studentRequest.getEmail(), studentId)) {
            throw new DuplicateFieldException("Email already exist!");
        }

        return student;
    }

    @Transactional(readOnly = true)
    public StudentResponse studentById(Integer id) {
        return this.studentRepository.findById(id)
                .map(c -> this.mapper.map(c, StudentResponse.class))
                .orElseThrow(() -> new ResourceNotFoundException("Student not found by student id=" + id));
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> studentResponsePage(String search, Pageable pageable) {
        Student student = new Student();
        student.setName(search);
        student.setEmail(search);
        student.setRoll(search);

        Example<Student> example = Example.of(
                student,
                matchingAny()
                        .withIgnoreCase("name", "email", "roll")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );


        return this.studentRepository.findAll(example, pageable)
                .map(t -> this.mapper.map(t, StudentResponse.class));
    }
}
