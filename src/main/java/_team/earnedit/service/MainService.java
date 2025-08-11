package _team.earnedit.service;

import _team.earnedit.dto.main.MainPageResponse;
import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.dto.wish.WishListResponse;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.Salary;
import _team.earnedit.entity.User;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.mapper.MainPageMapper;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.SalaryRepository;
import _team.earnedit.repository.StarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final SalaryRepository salaryRepository;
    private final StarRepository starRepository;
    private final PieceRepository pieceRepository;
    private final EntityFinder entityFinder;
    private final MainPageMapper mainPageMapper;

    @Transactional(readOnly = true)
    public MainPageResponse getInfo(Long userId) {
        User user = entityFinder.getUserOrThrow(userId);
        Salary salary = salaryRepository.findByUserId(userId).orElse(null);

        // 유저 정보 매핑
        MainPageResponse.UserInfo userInfo = mainPageMapper.toUserInfo(user, salary);

        // Top5 위시
        List<WishListResponse> starWishList = getStarWishList(userId);

        // 가장 최근 획득한 조각
        PieceResponse recentPiece = getRecentPiece(userId);

        // 응답 객체 생성
        return MainPageResponse.builder()
                .starWishes(starWishList)
                .userInfo(userInfo)
                .pieceInfo(recentPiece)
                .build();
    }

    // 해당 유저의 Star 리스트 랭크 순서대로 조회
    private List<WishListResponse> getStarWishList(Long userId) {
        return starRepository.findByUserIdOrderByRankAsc(userId)
                .stream()
                .map(star -> mainPageMapper.toWishListResponse(star.getWish()))
                .toList();
    }

    // 가장 최근 획득한 조각
    private PieceResponse getRecentPiece(Long userId) {
        return pieceRepository.findTopByUserIdOrderByCollectedAtDesc(userId)
                .map(mainPageMapper::toPieceResponse)
                .orElse(null);
    }
}
