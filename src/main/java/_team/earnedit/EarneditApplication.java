package _team.earnedit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
@Slf4j
public class EarneditApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(EarneditApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void logCurrentTime() {
		ZonedDateTime now = ZonedDateTime.now();
		log.info("서버 시작 시각: {}", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));
	}
}
