package _team.earnedit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalaryType type;

    private Boolean severance;

    @Column(nullable = false)
    private Long amount;

    private Integer dependentCount;

    private Long taxExemptAmount;

    @Column(nullable = false)
    private Boolean tax = false;

    @Column(nullable = false)
    private Double amountPerSec;

    public enum SalaryType {
        MONTH,
        YEAR,
        NONE
    }
}
