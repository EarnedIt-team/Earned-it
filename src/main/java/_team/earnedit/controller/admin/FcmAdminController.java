package _team.earnedit.controller.admin;

import _team.earnedit.entity.MessageTemplate;
import _team.earnedit.repository.MessageTemplateRepository;
import _team.earnedit.service.notification.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin/fcm")
@RequiredArgsConstructor
public class FcmAdminController {

    private final FcmService fcmService;
    private final MessageTemplateRepository templateRepo;

    // 관리자 폼 (GET)
    @GetMapping("/send")
    public String sendForm(Model model) {
        model.addAttribute("templates", templateRepo.findByActiveTrue()); // 관리자에서 템플릿 목록 보기용
        model.addAttribute("defaultTopic", "global_notifications");
        return "admin/fcm-send";
    }

    // 즉시 전송: title/body 로 글로벌(또는 topic) 전송 (폼에서 POST)
    @PostMapping("/send")
    public String sendImmediate(
            @RequestParam String topic,
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam(required = false) Map<String, String> data,
            Model model
    ) {
        try {
            // null/empty 처리를 간단히
            if (topic == null || topic.isBlank()) topic = "global_notifications";
            fcmService.sendToTopic(topic, title, body, data);
            model.addAttribute("success", "메시지 전송 성공");
        } catch (Exception e) {
            log.error("Immediate send failed", e);
            model.addAttribute("error", "전송 실패: " + e.getMessage());
        }
        model.addAttribute("templates", templateRepo.findByActiveTrue());
        model.addAttribute("defaultTopic", topic);
        return "admin/fcm-send";
    }

    // 템플릿 기반 전송: 선택된 템플릿을 global 토픽으로 전송 (관리자 버튼용)
    @PostMapping("/send/template/{id}")
    public String sendTemplateGlobal(@PathVariable Long id, Model model) {
        Optional<MessageTemplate> t = templateRepo.findById(id);
        if (t.isEmpty()) {
            model.addAttribute("error", "템플릿을 찾을 수 없음");
        } else {
            try {
                fcmService.sendGlobal(t.get());
                model.addAttribute("success", "템플릿 전송 성공");
            } catch (Exception e) {
                log.error("Template send failed", e);
                model.addAttribute("error", "템플릿 전송 실패: " + e.getMessage());
            }
        }
        model.addAttribute("templates", templateRepo.findByActiveTrue());
        model.addAttribute("defaultTopic", "global_notifications");
        return "admin/fcm-send";
    }
}
