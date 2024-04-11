package everycoding.nalseebackend.firebase.alarm;

import everycoding.nalseebackend.firebase.FcmService;
import everycoding.nalseebackend.firebase.alarm.domain.Alarm;
import everycoding.nalseebackend.firebase.alarm.domain.AlarmType;
import everycoding.nalseebackend.firebase.alarm.dto.AlarmDto;
import everycoding.nalseebackend.firebase.dto.FcmSendDto;
import everycoding.nalseebackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final FcmService fcmService;

    public List<AlarmDto> getAllAlarm(User user) {
        List<Alarm> alarmsByUser = alarmRepository.findAlarmsByUser(user);
        return alarmsByUser.stream().map(alarm -> {
            AlarmDto.AlarmDtoBuilder builder = AlarmDto.builder()
                    .message(alarm.getMessage())
                    .senderId(alarm.getSenderId())
                    .senderName(alarm.getSenderName())
                    .createAt(alarm.getCreateDate())
                    .senderImage(alarm.getSenderImg());

            if (alarm.getCommentId() != null) {
                builder.commentId(alarm.getCommentId());
            }
            if (alarm.getPostId() != null) {
                builder.postId(alarm.getPostId());
            }
            if (alarm.getOwnerId() != null) {
                builder.userId(alarm.getOwnerId());
            }

            return builder.build();
        }).collect(Collectors.toList());
    }

    public void sendFcmAndSaveAlarm(User owner, User me, String username, String userToken, String title, String message, Long targetId, AlarmType alarmType) throws IOException {
        if (!owner.getId().equals(me.getId())) {
            if (!userToken.equals("error")) {
                FcmSendDto fcmSendDto = FcmSendDto.builder()
                        .token(userToken)
                        .title(title)
                        .body(message)
                        .build();

                switch (alarmType) {
                    case COMMENT:
                        fcmSendDto.setCommentId(targetId);
                        break;
                    case POST:
                        fcmSendDto.setPostId(targetId);
                        break;
                    case USER:
                        fcmSendDto.setUserId(targetId);
                        break;
                }

                fcmService.sendMessageTo(fcmSendDto);
            }

            Alarm alarm = Alarm.builder()
                    .senderId(me.getId())
                    .senderImg(me.getPicture())
                    .senderName(username)
                    .user(owner)
                    .message(message)
                    .build();

            switch (alarmType) {
                case COMMENT:
                    alarm.setCommentId(targetId);
                    break;
                case POST:
                    alarm.setPostId(targetId);
                    break;
                case USER:
                    alarm.setOwnerId(targetId);
                    break;
            }

            alarmRepository.save(alarm);
        }
    }
}
