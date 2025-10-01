package _team.earnedit.repository;

import _team.earnedit.entity.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {
    List<MessageTemplate> findByCategoryAndActiveTrue(MessageTemplate.Category category);

    List<MessageTemplate> findByActiveTrue();
}
