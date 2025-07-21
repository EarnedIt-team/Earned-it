package _team.earnedit.service;

import _team.earnedit.dto.auth.SignUpRequestDto;
import _team.earnedit.dto.auth.SignUpResponseDto;
import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public SignUpResponseDto signUp(SignUpRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String nickname = generateUniqueNickname();

        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        User user = userRepository.save(
                User.builder()
                        .email(email)
                        .password(password)
                        .nickname(nickname)
                        .provider(User.Provider.LOCAL)
                        .status(User.Status.ACTIVE)
                        .isDarkMode(requestDto.getIsDarkMode())
                        .isPublic(requestDto.getIsPublic())
                        .build()
        );

        return new SignUpResponseDto(user.getId(), user.getEmail(), user.getNickname());
    }

    private String generateUniqueNickname() {
        Random random = new Random();
        String nickname;
        do {
            int randomNumber = random.nextInt(1_000_000);  // 0 ~ 999,999
            nickname = "익명의사용자" + randomNumber;
        } while (userRepository.existsByNickname(nickname));
        return nickname;
    }

}