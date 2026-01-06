package com.sdlc.pro.smms.dto;

import com.sdlc.pro.smms.enums.Semester;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OfferedCourseRequest {
    private Integer id;

    @NotBlank
    private String courseCode;

    @NotBlank
    @Email
    private String teacherEmail;

    @NotNull
    @Positive
    private Integer year;

    @NotNull
    private Semester semester;
}
