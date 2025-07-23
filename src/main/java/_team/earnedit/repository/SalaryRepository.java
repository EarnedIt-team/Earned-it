package _team.earnedit.repository;

import _team.earnedit.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    boolean existsByUserId(Long id);

}
