package _team.earnedit.service;

import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User signup(String email, String password, String nickname) {

        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .status(User.Status.ACTIVE)
                .provider(User.Provider.LOCAL)
                .isDarkMode(false)
                .isPublic(false)
                .build();

        return userRepository.save(user);
    }
}
