package everycoding.nalseebackend.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class UserStatusController {

    private final ConcurrentHashMap<Long, Boolean> authenticatedUsers;
    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam List<Long> userIds) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        // 각 유저 ID에 대해 emitter를 등록
        userIds.forEach(userId -> userEmitters.put(String.valueOf(userId), emitter));

        // Emitter의 이벤트 핸들러 설정
        emitter.onCompletion(() -> userIds.forEach(userId -> userEmitters.remove(String.valueOf(userId))));
        emitter.onTimeout(() -> userIds.forEach(userId -> userEmitters.remove(String.valueOf(userId))));
        emitter.onError((e) -> userIds.forEach(userId -> userEmitters.remove(String.valueOf(userId))));

        // 초기 상태를 클라이언트에 푸시
        userIds.forEach(userId -> {
            Boolean isConnected = authenticatedUsers.getOrDefault(userId, false);
            notifyUserStatusChange(userId, isConnected);
        });

        return emitter;
    }

    public void notifyUserStatusChange(Long userId, boolean isConnected) {
        SseEmitter emitter = userEmitters.get(String.valueOf(userId));
        if (emitter != null) {
            try {
                // 사용자 상태 변경 이벤트를 보냄
                emitter.send(SseEmitter.event().name("userStatus").data(Map.of(userId, isConnected)));
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }
    }

    // 사용자의 상태가 변경될 때 호출되는 메서드
    public void updateUserStatus(Long userId, boolean status) {
        authenticatedUsers.put(userId, status);
        notifyUserStatusChange(userId, status);
    }
}
