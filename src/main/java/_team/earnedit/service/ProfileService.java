package _team.earnedit.service;

import _team.earnedit.dto.profile.*;
import _team.earnedit.dto.star.StarSummaryResponse;
import _team.earnedit.entity.Salary;
import _team.earnedit.entity.Star;
import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.profile.ProfileException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.global.util.SalaryCalculator;
import _team.earnedit.repository.SalaryRepository;
import _team.earnedit.repository.StarRepository;
import _team.earnedit.repository.UserReportRepository;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final SalaryRepository salaryRepository;
    private final SalaryCalculator salaryCalculator;
    private final FileUploadService fileUploadService;
    private final EntityFinder entityFinder;
    private final StarRepository starRepository;
    private final UserReportRepository userReportRepository;

    /*
     ******** 수익 관련 ********
     */

    // 수익 정보 입력 + 수정 (덮어쓰기)
    @Transactional
    public SalaryResponseDto updateSalary(long userId, SalaryRequestDto requestDto) {
        Long amount = requestDto.getAmount();
        Integer payday = requestDto.getPayday();
        double amountPerSec = salaryCalculator.calculateAmountPerSec(amount);

        Optional<Salary> existing = salaryRepository.findByUserId(userId);

        Salary salary = existing
                .map(s -> {
                    s.setAmount(amount);
                    s.setPayday(payday);
                    s.updateAmountPerSec(amountPerSec);
                    s.setTax(false);
                    s.setType(Salary.SalaryType.MONTH);
                    return s;
                })
                .orElseGet(() -> Salary.builder()
                        .user(User.builder().id(userId).build())
                        .type(Salary.SalaryType.MONTH)
                        .amount(amount)
                        .tax(false)
                        .amountPerSec(amountPerSec)
                        .payday(payday)
                        .build());

        Salary saved = salaryRepository.save(salary);
        return SalaryResponseDto.from(saved);
    }

    // 수익 조회
    @Transactional(readOnly = true)
    public SalaryResponseDto getSalary(Long userId) {
        Salary salary = entityFinder.getSalaryOrThrow(userId);

        return SalaryResponseDto.from(salary);
    }


    /*
     ******** 프로필 관련 ********
     */

    // 프로필페이지 기본 정보 조회
    @Transactional(readOnly = true)
    public ProfileInfoResponseDto getProfile(Long userId) {
        User user = entityFinder.getUserOrThrow(userId);
        Salary salary = entityFinder.getSalaryOrThrow(userId);

        return ProfileInfoResponseDto.builder()
                .userId(user.getId())
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .monthlySalary(salary.getAmount())
                .build();
    }

    // 닉네임 변경
    @Transactional
    public void updateNickname(Long userId, NicknameRequestDto requestDto) {
        String nickname = requestDto.getNickname();

        if (userRepository.existsByNickname(nickname)) {
            throw new ProfileException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        User user = entityFinder.getUserOrThrow(userId);

        user.updateNickname(nickname);
    }


    // 프로필 사진 변경
    @Transactional
    public void updateProfileImage(Long userId, MultipartFile profileImage) {
        User user = entityFinder.getUserOrThrow(userId);

        String imageUrl = fileUploadService.uploadFile(profileImage);

        user.updateProfileImage(imageUrl);
    }

    // 프로필 사진 삭제
    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = entityFinder.getUserOrThrow(userId);

        user.updateProfileImage(null);
    }


    // 프로필 공개범위 설정
    @Transactional
    public void updateVisibility(Long userId, UpdateVisibilityRequestDto requestDto) {
        User user = entityFinder.getUserOrThrow(userId);

        // 공개로 전환하려는 경우, 차단 검사 (임계치:5회 이상이면 공개전환 불가)
        if (Boolean.TRUE.equals(requestDto.getIsPublic())) {
            long totalReports = userReportRepository.countByReportedUser_Id(user.getId());
            if (totalReports >= 5) {
                throw new ProfileException(ErrorCode.VISIBILITY_LOCKED);
            }
        }

        user.updateVisibility(requestDto.getIsPublic());
    }


    @Transactional(readOnly = true)
    public List<PublicUserInfoResponse> randomUsers(Long userId, long count) {
        entityFinder.getUserOrThrow(userId);

        // 프로필 공개 상태인 유저 count 명 조회
        List<User> randomPublicUsers = userRepository.findRandomPublicUsersForMe(userId,count);

        return randomPublicUsers.stream().map(user ->
                        PublicUserInfoResponse.builder()
                                .userId(user.getId())
                                .nickname(user.getNickname())
                                .profileImage(user.getProfileImage())
                                .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public ProfileWithStarResponse getProfileWithStarList(Long loggedInUserId, long userId) {
        entityFinder.getUserOrThrow(loggedInUserId);

        List<Star> starList = starRepository.findByUserId(userId);
        User findUser = entityFinder.getUserOrThrow(userId);

        Salary salary = entityFinder.getSalaryOrThrow(userId);

        OtherUserProfileResponse userInfo = OtherUserProfileResponse.builder()
                .userId(findUser.getId())
                .nickname(findUser.getNickname())
                .profileImage(findUser.getProfileImage())
                .monthlySalary(salary.getAmount())
                .amountPerSec(salary.getAmountPerSec())
                .payday(salary.getPayday())
                .build();

        List<StarSummaryResponse> starSummaryList = starList.stream()
                .map(star -> StarSummaryResponse.builder()
                        .starId(star.getId())
                        .userId(star.getUser().getId())
                        .name(star.getWish().getName())
                        .vendor(star.getWish().getVendor())
                        .price(star.getWish().getPrice())
                        .itemImage(star.getWish().getItemImage())
                        .starred(star.getWish().isStarred())
                        .isBought(star.getWish().isBought())
                        .rank(star.getRank())
                        .build())
                .toList();

        return ProfileWithStarResponse.builder()
                .userInfo(userInfo)
                .starList(starSummaryList)
                .build();
    }

}
