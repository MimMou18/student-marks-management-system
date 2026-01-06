package com.sdlc.pro.smms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"enrollment_id", "test_no"}
        )
)
@Setter
@Getter
@NoArgsConstructor
public class ClassTestMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Column(nullable = false)
    private Integer testNo;   // CT-1, CT-2

    @Column(nullable = false)
    private Double marks;

    public ClassTestMark(Enrollment enrollment, Integer testNo, Double marks) {
        this.enrollment = enrollment;
        this.testNo = testNo;
        this.marks = marks;
    }
}
