package _team.earnedit.repository;

import _team.earnedit.entity.Term;
import _team.earnedit.entity.Term.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {

    Optional<Term> findByUserIdAndType(Long userId, Type type);

    boolean existsByUserId(Long id);
}
