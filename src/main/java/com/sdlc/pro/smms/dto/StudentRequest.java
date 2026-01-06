package com.sdlc.pro.smms.dto;

import com.sdlc.pro.smms.enums.Semester;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentRequest {
    private Integer id;

    @NotBlank
    @Size(max = 10)
    private String roll;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Email
    private String email;

    @Size(min = 6, max = 50)
    private String password;

    @NotNull
    private Semester semester;
}
