package _team.timoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class TimoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimoneyApplication.class, args);
	}

}
