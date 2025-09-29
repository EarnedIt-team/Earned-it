package _team.earnedit.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_PROJECT_ID}")
    private String projectId;

    @Value("${FIREBASE_CLIENT_EMAIL}")
    private String clientEmail;

    @Value("${FIREBASE_PRIVATE_KEY_BASE64}")
    private String privateKeyBase64;

    @PostConstruct
    public void initialize() {
        try {
            log.info("Initializing Firebase...");

            // base64 디코딩
            String privateKey = new String(Base64.getDecoder().decode(privateKeyBase64), StandardCharsets.UTF_8);

            // Firebase JSON 구성
            Map<String, Object> credentialsMap = new HashMap<>();
            credentialsMap.put("type", "service_account");
            credentialsMap.put("project_id", projectId);
            credentialsMap.put("private_key_id", "dummy-key-id");
            credentialsMap.put("private_key", privateKey);
            credentialsMap.put("client_email", clientEmail);
            credentialsMap.put("client_id", "dummy-client-id");
            credentialsMap.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
            credentialsMap.put("token_uri", "https://oauth2.googleapis.com/token");
            credentialsMap.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
            credentialsMap.put("client_x509_cert_url",
                    "https://www.googleapis.com/robot/v1/metadata/x509/" + clientEmail.replace("@", "%40"));

            InputStream serviceAccount = new ByteArrayInputStream(new Gson()
                    .toJson(credentialsMap).getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully!");
            } else {
                log.info("FirebaseApp already initialized.");
            }

        } catch (Exception e) {
            log.error("Failed to initialize Firebase!", e);
        }
    }
}
