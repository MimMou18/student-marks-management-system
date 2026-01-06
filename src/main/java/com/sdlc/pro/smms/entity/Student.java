package com.sdlc.pro.smms.entity;

import com.sdlc.pro.smms.enums.Semester;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "roll")
})
@Setter
@Getter
public class Student extends AppUser {
    private String roll;

    @Column(name = "semester")
    @Enumerated(EnumType.STRING)
    private Semester semester;

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();
}
