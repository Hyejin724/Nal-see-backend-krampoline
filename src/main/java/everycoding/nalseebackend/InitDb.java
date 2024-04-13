package everycoding.nalseebackend;

import everycoding.nalseebackend.comment.domain.Comment;
import everycoding.nalseebackend.post.PostRepository;
import everycoding.nalseebackend.post.domain.Post;
import everycoding.nalseebackend.user.UserRepository;
import everycoding.nalseebackend.user.domain.*;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final BCryptPasswordEncoder passwordEncoder;
        private final UserRepository userRepository;
        private final PostRepository postRepository;
        private Random rd = new Random();

        public void dbInit() {

            String[] names = {
                    "홍길동",
                    "김철수",
                    "김영희",
                    "이영희",
                    "박철수",
                    "이철수",
                    "박영희",
                    "이길동",
                    "김길동",
                    "박길동",
                    "제임스"
            };

            for (int i = 0; i < 10; i++) {
                User user = User.builder()
                        .username(names[i])
                        .email(i + "@a.com")
                        .password(passwordEncoder.encode("1234"))
                        .picture("https://placeholder.co/50x50")
                        .role("USER")
                        .build();
                em.persist(user);
            }

            List<String> list = new ArrayList<>();
            list.add("https://placehold.co/350x265");
            list.add("https://placehold.co/350x265");
            list.add("https://placehold.co/350x265");

            String[] contents = {
                    "오늘 비도 많이오고 추워서 두틈하게 코트랑 우산챙겨서 나왔어요. 여러분도 비 맞지 않게 조심하세요!",
                    "오늘은 날씨가 좋네요. 햇살도 따뜻하고 바람도 시원해요. 산책하기 딱 좋은 날씨네요.",
                    "오늘은 덥네요. 더위 조심하세요!",
                    "오늘은 추워요. 따뜻하게 입고 나가세요.",
                    "오늘은 눈이 많이 오네요. 미끄럼 조심하세요.",
                    "오늘은 바람이 많이 불어요. 모자 꼭 쓰고 다니세요."
            };

            String[] weathers = {"Thunderstorm", "Rain", "Snow", "Fog", "Clear", "Clouds"};

            String[] places = {
                    "서울시 강남구",
                    "서울시 강북구",
                    "서울시 강서구",
                    "서울시 강동구",
                    "서울시 중구",
                    "서울시 용산구",
                    "서울시 성동구",
                    "서울시 성북구",
                    "서울시 동대문구",
                    "서울시 동작구"
            };

            Constitution[] constitutions = {Constitution.normal, Constitution.coldSensitive, Constitution.heatSensitive};
            List<FashionStyle> styles = Stream.of(FashionStyle.values()).toList();
            Gender[] genders = {Gender.M, Gender.F};

            for (int i = 0; i < 100; i++) {
                Post post = Post.builder()
                        .pictureList(list)
                        .content(contents[rd.nextInt(contents.length)])
                        .user(userRepository.findById(rd.nextLong(10)+1).orElseThrow())
                        .weather(weathers[rd.nextInt(weathers.length)])
                        .temperature(rd.nextInt(300)/10.0)
                        .address(places[rd.nextInt(places.length)])
                        .longitude(rd.nextInt(9000)/100.0)
                        .latitude(rd.nextInt(9000)/100.0)
                        .userInfo(UserInfo.builder()
                                .height(160 + rd.nextInt(300)/10.0)
                                .weight(50 + rd.nextInt(500)/10.0)
                                .constitution(constitutions[rd.nextInt(constitutions.length)])
                                .style(styles.subList(i%7, i%7+rd.nextInt(4)))
                                .gender(genders[i%2])
                                .build())
                        .build();
                em.persist(post);
            }

            String[] commentContents = {
                    "안녕하세요",
                    "반갑습니다",
                    "ㅋㅋㅋㅋㅋㅋ",
                    "옷예쁘네요",
                    "멋있어요",
                    "ㅎㅎㅎㅎ",
                    "힙하시네요",
                    "사진 잘나왔어요",
                    "와...",
                    "좋은 하루 보네세요"
            };

            for (int i = 0; i < 500; i++) {
                Comment comment = Comment.builder()
                        .content(commentContents[rd.nextInt(commentContents.length)])
                        .user(userRepository.findById(rd.nextLong(10)+1).orElseThrow())
                        .post(postRepository.findById(rd.nextLong(100)+1).orElseThrow())
                        .build();
                em.persist(comment);
            }
        }
    }
}
