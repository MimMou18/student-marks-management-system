package com.sdlc.pro.smms.entity;

import com.sdlc.pro.smms.enums.Semester;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"course_id", "year", "semester"}
                )
        }
)
@Setter
@Getter
public class CourseOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "semester")
    @Enumerated(EnumType.STRING)
    private Semester semester;

    @OneToMany(mappedBy = "courseOffering")
    private List<Enrollment> enrollments = new ArrayList<>();
}
