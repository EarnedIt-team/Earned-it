package _team.earnedit.service;

import _team.earnedit.dto.term.TermRequestDto;
import _team.earnedit.entity.Term;
import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.term.TermException;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.repository.TermRepository;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final UserRepository userRepository;

    @Transactional
    public void agreeToTerms(Long userId, List<TermRequestDto> requestDtos) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        for (TermRequestDto dto : requestDtos) {
            Term.Type type = dto.getType();
            boolean isChecked = dto.isChecked();

            // 필수 약관
            if (isRequiredTerm(type) && !isChecked) {
                throw new TermException(ErrorCode.TERM_MUST_BE_CHECKED);
            }

            Term existingTerm = termRepository.findByUserIdAndType(userId, type).orElse(null);

            if (existingTerm != null) {
                // 값이 다르면 업데이트
                if (existingTerm.isChecked() != isChecked) {
                    existingTerm.setChecked(isChecked);
                    termRepository.save(existingTerm);
                }
            } else {
                // 기존 데이터 없으면 새로 저장
                Term term = Term.builder()
                        .user(user)
                        .type(type)
                        .isChecked(isChecked)
                        .build();
                termRepository.save(term);
            }
        }
    }

    // 필수 약관 목록
    private boolean isRequiredTerm(Term.Type type) {
        return type == Term.Type.SERVICE_REQUIRED;
    }

}
