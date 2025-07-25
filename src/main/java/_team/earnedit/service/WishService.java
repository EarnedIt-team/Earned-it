package _team.earnedit.service;

import _team.earnedit.dto.wish.WishAddRequest;
import _team.earnedit.dto.wish.WishAddResponse;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.repository.UserRepository;
import _team.earnedit.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishService {
    private final WishRepository wishRepository;
    private final UserRepository userRepository;

    public WishAddResponse addWish(WishAddRequest wishAddRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // wish 객체 생성
        Wish wish = Wish.builder()
                .user(user)
                .price(wishAddRequest.getPrice())
                .url(wishAddRequest.getUrl())
                .itemImage(wishAddRequest.getItemImage())
                .name(wishAddRequest.getName())
                .vendor(wishAddRequest.getVendor())
                .build();
        wishRepository.save(wish);

        return WishAddResponse.builder()
                .wishId(wish.getId())
                .createdAt(wish.getCreatedAt())
                .build();
    }
}
