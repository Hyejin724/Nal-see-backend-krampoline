package everycoding.nalseebackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ConcurrentHashMap<Long, Boolean> authenticatedUsers() {
        return new ConcurrentHashMap<>();
    }
}
