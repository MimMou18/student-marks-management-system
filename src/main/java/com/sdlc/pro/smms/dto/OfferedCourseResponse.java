package com.sdlc.pro.smms.dto;

import com.sdlc.pro.smms.enums.Semester;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OfferedCourseResponse {
    private Integer id;
    private String courseCode;
    private String courseTitle;
    private String courseTeacher;
    private Semester semester;
    private Integer year;
}
