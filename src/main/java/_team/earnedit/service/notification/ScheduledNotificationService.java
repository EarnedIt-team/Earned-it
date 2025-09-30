package _team.earnedit.service.notification;

import _team.earnedit.entity.MessageTemplate;
import _team.earnedit.entity.MessageTemplate.Category;
import _team.earnedit.repository.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledNotificationService {

    private final MessageTemplateRepository templateRepository;
    private final FcmService fcmService;
    private final Random rnd = new Random();

    // 출근시간 (09:00 KST)
    @Scheduled(cron = "0 0 9 * * *")
    public void sendMorningQuote() {
        sendRandom(Category.QUOTE);
    }

    // 점심시간 (12:30 KST)
    @Scheduled(cron = "0 30 12 * * *")
    public void sendLunch() {
        sendRandom(Category.LUNCH);
    }

    // 퇴근시간 (19:00 KST)
    @Scheduled(cron = "0 0 19 * * *")
    public void sendEveningQuote() {
        sendRandom(Category.ENCOURAGE);
    }

    // db에 저장된 카테고리별 메시지 중 랜덤 발송
    private void sendRandom(Category category) {
        List<MessageTemplate> list = templateRepository.findByCategoryAndActiveTrue(category);
        if (list.isEmpty()) {
            log.warn("No message templates for category={}", category);
            return;
        }
        MessageTemplate picked = list.get(rnd.nextInt(list.size()));
        log.info("Picked message id={} category={}", picked.getId(), category);
        fcmService.sendGlobal(picked);
    }
}
