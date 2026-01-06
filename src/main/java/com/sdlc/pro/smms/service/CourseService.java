package com.sdlc.pro.smms.service;

import com.sdlc.pro.smms.dto.CourseRequest;
import com.sdlc.pro.smms.dto.CourseResponse;
import com.sdlc.pro.smms.entity.Course;
import com.sdlc.pro.smms.exception.DuplicateFieldException;
import com.sdlc.pro.smms.exception.ResourceNotFoundException;
import com.sdlc.pro.smms.repository.CourseRepository;
import com.sdlc.pro.smms.util.ExceptionTranslator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.*;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final ModelMapper mapper;
    private final CourseRepository courseRepository;

    public void saveCourse(CourseRequest courseRequest) {
        if (this.courseRepository.existsByCode(courseRequest.getCode())) {
            throw new DuplicateFieldException("Course code already exist!");
        }

        Course course = mapper.map(courseRequest, Course.class);
        try {
            courseRepository.save(course);
        } catch (DataIntegrityViolationException ex) {
            throw ExceptionTranslator.handleDuplicateConstraint(ex);
        }
    }

    @Transactional(readOnly = true)
    public CourseResponse courseById(Integer id) {
        return courseRepository.findById(id)
                .map(c -> mapper.map(c, CourseResponse.class))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found by course id=" + id));
    }

    @Transactional(readOnly = true)
    public Page<CourseResponse> courseResponsePage(String search, Pageable pageable) {
        Course course = new Course();
        course.setCode(search);
        course.setTitle(search);
        Example<Course> example = Example.of(
                course,
                matchingAny()
                        .withIgnoreCase("code", "title").withStringMatcher(StringMatcher.CONTAINING)
        );


        return courseRepository.findAll(example, pageable).map(c -> mapper.map(c, CourseResponse.class));
    }

    @Transactional(readOnly = true)
    public List<String> getAllCourseCode() {
        return this.courseRepository.findAllCourseCode();
    }

}
