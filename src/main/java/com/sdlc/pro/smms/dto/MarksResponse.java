package com.sdlc.pro.smms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MarksResponse {
    private String courseCode;
    private String courseTitle;
    private List<CTMark> ctMarks;
}
