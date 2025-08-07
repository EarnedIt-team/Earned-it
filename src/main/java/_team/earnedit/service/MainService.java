package _team.earnedit.service;

import _team.earnedit.dto.main.MainPageResponse;
import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.dto.wish.WishListResponse;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.Salary;
import _team.earnedit.entity.Star;
import _team.earnedit.entity.Wish;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.SalaryRepository;
import _team.earnedit.repository.StarRepository;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainService {

    private final UserRepository userRepository;
    private final SalaryRepository salaryRepository;
    private final StarRepository starRepository;
    private final PieceRepository pieceRepository;
    private final EntityFinder entityFinder;

    @Transactional(readOnly = true)
    public MainPageResponse getInfo(Long userId) {
        entityFinder.getUserOrThrow(userId);

        Optional<Salary> salary = salaryRepository.findByUserId(userId);

        boolean hasSalary = salary.isPresent();

        // 유저 정보 (초당 수익, 수익 유무)
        MainPageResponse.UserInfo userInfo = MainPageResponse.UserInfo.builder()
                .amount(salary.map(Salary::getAmount).orElse(0L))
                .amountPerSec(salary.map(Salary::getAmountPerSec).orElse(0.0))
                .payday(salary.map(Salary::getPayday).orElse(0))
                .hasSalary(hasSalary)
                .build();

        // Top5 정보 조회
        List<Star> stars = starRepository.findByUserIdOrderByRankAsc(userId);

        List<WishListResponse> starWishList = stars.stream()
                .map(star -> {
                    Wish wish = star.getWish();
                    return WishListResponse.from(wish);  // 또는 WishListResponse 생성자 활용
                })
                .toList();

        // 가장 최근 조각 1개 조회하여 삽입
        // Piece recentPiece = pieceRepository.findTopByUserIdOrderByCollectedAtDesc(userId)
        //.orElseThrow(() -> new PieceException(ErrorCode.PIECE_NOT_FOUND));

        // 프론트 측 요청으로 예외를 던지지 않고, null 또는 빈값으로 응답
        Optional<Piece> recentPiece = pieceRepository.findTopByUserIdOrderByCollectedAtDesc(userId);

        if (recentPiece.isPresent()) {
            // PieceResponse 객체 생성
            PieceResponse pieceResponse = PieceResponse.builder()
                    .pieceId(recentPiece.get().getId())
                    .collectedAt(recentPiece.get().getCollectedAt())
                    .price(recentPiece.get().getItem().getPrice())
                    .rarity(recentPiece.get().getItem().getRarity())
                    .name(recentPiece.get().getItem().getName())
                    .image(recentPiece.get().getItem().getImage())
                    .vendor(recentPiece.get().getItem().getVendor())
                    .description(recentPiece.get().getItem().getDescription())
                    .build();

            // 응답 객체 생성
            return MainPageResponse.builder()
                    .starWishes(starWishList)
                    .userInfo(userInfo)
                    .pieceInfo(pieceResponse)
                    .build();
        }
        // 응답 객체 생성
        return MainPageResponse.builder()
                .starWishes(starWishList)
                .userInfo(userInfo)
                .pieceInfo(null)
                .build();
    }
}
