package _team.earnedit.service;

import _team.earnedit.dto.main.MainPageResponse;
import _team.earnedit.dto.wish.WishListResponse;
import _team.earnedit.entity.Salary;
import _team.earnedit.entity.Star;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.salary.SalaryException;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.repository.SalaryRepository;
import _team.earnedit.repository.StarRepository;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final UserRepository userRepository;
    private final SalaryRepository salaryRepository;
    private final StarRepository starRepository;

    @Transactional(readOnly = true)
    public MainPageResponse getInfo(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Salary salary = salaryRepository.findByUserId(userId)
                .orElseThrow(() -> new SalaryException(ErrorCode.SALARY_NOT_FOUND));

        boolean hasSalary = salaryRepository.existsByUserId(userId);

        // 유저 정보 (초당 수익, 수익 유무)
        MainPageResponse.UserInfo userInfo = MainPageResponse.UserInfo.builder()
                .amount(salary.getAmount())
                .hasSalary(hasSalary)
                .amountPerSec(salary.getAmountPerSec())
                .payday(salary.getPayday())
                .build();

        // Top5 정보 조회
        List<Star> stars = starRepository.findByUserIdOrderByRankAsc(userId);

        List<WishListResponse> starWishList = stars.stream()
                .map(star -> {
                    Wish wish = star.getWish();
                    return WishListResponse.from(wish);  // 또는 WishListResponse 생성자 활용
                })
                .toList();

        // 응답 객체 생성
        return MainPageResponse.builder()
                .starWishes(starWishList)
                .userInfo(userInfo)
                .build();
    }
}
