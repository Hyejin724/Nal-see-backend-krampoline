package everycoding.nalseebackend.firebase.alarm;

import everycoding.nalseebackend.firebase.alarm.domain.Alarm;
import everycoding.nalseebackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findAlarmsByUser (User user);
}
