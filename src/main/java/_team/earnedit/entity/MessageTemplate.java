package _team.earnedit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_templates")
public class MessageTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title; // 메인 타이틀

    @Column(nullable=false, length = 1000)
    private String body;  // 서브타이틀(멘트)

    @Column(length = 64)
    private String category; // "quote", "lunch", "encourage" 등

    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    public MessageTemplate() {}
    public MessageTemplate(String title, String body, String category) {
        this.title = title;
        this.body = body;
        this.category = category;
    }
}