package _team.earnedit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class EarneditApplication {

	public static void main(String[] args) {
		SpringApplication.run(EarneditApplication.class, args);
	}

}
