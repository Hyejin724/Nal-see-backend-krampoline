package everycoding.nalseebackend.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DeleteUserNotificationService {

    private final WebClient webClient;

    public DeleteUserNotificationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://nalsee.site:8090/").build();
//        this.webClient = webClientBuilder.baseUrl("http://localhost:8090/").build();
    }

    public Mono<Void> checkDeleteUser(Long userId) {
        log.info("유저 삭제 webClient 보냈다 ={}", userId);
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/delete-user")
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .bodyToMono(Void.class);

    }
}
