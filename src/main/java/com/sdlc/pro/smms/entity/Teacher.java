package com.sdlc.pro.smms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Setter
@Getter
public class Teacher extends AppUser {
    private String designation;

    @OneToMany(mappedBy = "teacher")
    private List<CourseOffering> courseOfferings = new ArrayList<>();
}
