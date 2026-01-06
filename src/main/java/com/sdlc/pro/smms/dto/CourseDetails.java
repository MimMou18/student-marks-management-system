package com.sdlc.pro.smms.dto;

import com.sdlc.pro.smms.enums.Semester;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CourseDetails {
    private String code;
    private String title;
    private Semester semester;
    private Integer year;
    private List<StudentInfo> students;

    public int getTotalStudent() {
        return students == null ? 0 : students.size();
    }

    @Setter
    @Getter
    public static class StudentInfo {
        private Long enrollmentId;
        private String roll;
        private String name;
        private List<CTMark> ctMarks;
    }
}
