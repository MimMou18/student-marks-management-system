package com.sdlc.pro.smms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CTMarksInfo {
    @NotNull
    private Long enrollmentId;

    @Valid
    private List<CTMark> marks = new ArrayList<>();
}
