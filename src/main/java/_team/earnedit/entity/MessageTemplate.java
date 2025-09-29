package _team.earnedit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_templates")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageTemplate {

    public enum Category {
        QUOTE, LUNCH, ENCOURAGE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title; // 메인 타이틀

    @Column(nullable=false, length = 1000)
    private String body;  // 서브타이틀(멘트)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private Category category;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}