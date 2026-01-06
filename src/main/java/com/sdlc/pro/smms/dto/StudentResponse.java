package com.sdlc.pro.smms.dto;

import com.sdlc.pro.smms.enums.Semester;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentResponse {
    private Integer id;
    private String roll;
    private String name;
    private String email;
    private Semester semester;
}
