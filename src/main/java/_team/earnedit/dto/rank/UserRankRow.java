package _team.earnedit.dto.rank;

public interface UserRankRow {
    Long getUserId();
    Long getRank();
    String getNickname();
    Long getScore();
    String getProfileImage();
    boolean isPublic();
}
