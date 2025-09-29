package _team.earnedit.service.fcm;

import _team.earnedit.entity.MessageTemplate;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class FcmService {

    private static final String GLOBAL_TOPIC = "global_notifications";

    // 토픽 기반 FCM 메시지 전송
    public void sendToTopic(String topic, MessageTemplate template, Map<String,String> data) {
        Notification notification = Notification.builder()
                .setTitle(template.getTitle())
                .setBody(template.getBody())
                .build();

        Message.Builder mb = Message.builder()
                .setTopic(topic)
                .setNotification(notification);

        if (data != null) {
            mb.putAllData(data);
        }

        try {
            String resp = FirebaseMessaging.getInstance().send(mb.build());
            log.info("FCM topic sent. topic={}, templateId={}, resp={}", topic, template.getId(), resp);
        } catch (FirebaseMessagingException e) {
            log.error("FCM topic send failed. topic={}, templateId={}", topic, template.getId(), e);
            throw new RuntimeException(e);
        }
    }

    // 전체 사용자에게 전송
    public void sendGlobal(MessageTemplate template) {
        sendToTopic(GLOBAL_TOPIC, template, null);
    }
}
