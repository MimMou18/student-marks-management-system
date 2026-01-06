package com.sdlc.pro.smms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    private Integer id;

    @NotBlank(message = "Course code must not be blank")
    @Size(max = 10, message = "Course code must not be more than 10 character")
    private String code;

    @NotBlank(message = "Course title must not be blank")
    @Size(max = 50, message = "Course title must not be more than 50 character")
    private String title;
}
