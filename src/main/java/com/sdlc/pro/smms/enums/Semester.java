package com.sdlc.pro.smms.enums;

import lombok.Getter;

@Getter
public enum Semester {
    S1_1("1-1"),
    S1_2("1-2"),
    S2_1("2-1"),
    S2_2("2-2"),
    S3_1("3-1"),
    S3_2("3-2"),
    S4_1("4-1"),
    S4_2("4-2");

    final String label;

    Semester(String label) {
        this.label = label;
    }
}
