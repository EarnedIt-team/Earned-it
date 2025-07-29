package _team.earnedit.repository;

import _team.earnedit.entity.Term;
import _team.earnedit.entity.Term.Type;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {

    boolean existsByUserIdAndType(Long userId, Type type);

}
