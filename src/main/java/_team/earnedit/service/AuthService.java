package _team.earnedit.service;

import _team.earnedit.dto.auth.SignUpRequestDto;
import _team.earnedit.dto.auth.SignUpResponseDto;
import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static _team.earnedit.entity.User.Status.ACTIVE;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public SignUpResponseDto signUp(SignUpRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String nickname = requestDto.getNickname();

        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        User user = userRepository.save(
                User.builder()
                        .email(email)
                        .password(password)
                        .nickname(nickname)
                        .provider(User.Provider.LOCAL)
                        .status(ACTIVE)
                        .build()
        );

        return new SignUpResponseDto(user.getId(), user.getEmail(), user.getNickname());
    }
}
