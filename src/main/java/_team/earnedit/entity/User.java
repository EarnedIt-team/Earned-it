package _team.earnedit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public enum Status {
        ACTIVE, DELETED
    }

    public enum Provider {
        LOCAL, KAKAO, APPLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Column(length = 255)
    private String password;

    @Column(length = 255)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime lastLoginAt;

    @Column
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column
    private String providerId;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDarkMode = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isPublic = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isCheckedIn = false;

    @Column(nullable = false)
    private long score = 0L;

    public void softDeleted() {
        this.status = Status.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String imageUrl) {
        this.profileImage = imageUrl;
    }

    public void updateVisibility(boolean isPublic) { this.isPublic = isPublic; }

    public void checkIn() { ////////////// 동훈님 코드에서 사용하십시오 ~
        this.isCheckedIn = true;
    }

    public void resetCheckIn() {
        this.isCheckedIn = false;
    }

    public void checkedInReward() {
        this.score += 10;
    }

    public void reward_S () {
        this.score += 10;
    }

    public void reward_A () {
        this.score += 7;
    }
    public void reward_B () {
        this.score += 5;
    }

    // 퍼즐 테마 완성 시 100 지급
    public void reward_CompleteTheme() {
        this.score+=100;
    }
}
