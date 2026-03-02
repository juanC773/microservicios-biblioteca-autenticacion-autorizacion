package co.analisys.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CirculacionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CirculacionServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
				request.getHeaders().set("Authorization", "Bearer " + jwt.getTokenValue());
			}
			return execution.execute(request, body);
		});
		return restTemplate;
	}
}
