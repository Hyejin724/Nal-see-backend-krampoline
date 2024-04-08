package everycoding.nalseebackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@EnableJpaAuditing
@SpringBootApplication
public class NalseeBackendApplication implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(NalseeBackendApplication.class);

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(NalseeBackendApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 여기에서 원하는 환경 변수나 속성을 로그로 출력
        logger.info("현재 환경: {}", env.getActiveProfiles());
        // 추가적으로 필요한 로그 출력
    }

}
