package everycoding.nalseebackend.post;

import everycoding.nalseebackend.post.domain.Post;
import everycoding.nalseebackend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    Page<Post> findByIdLessThan(Long lastPostId, Pageable pageable);

    Page<Post> findByUserAndIdLessThan(User user, Long lastPostId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.latitude >= :bottomLeftLat AND p.latitude <= :topRightLat " +
            "AND p.longitude >= :bottomLeftLong AND p.longitude <= :topRightLong")
    List<Post> findByLocationWithin(Double bottomLeftLat, Double bottomLeftLong, Double topRightLat, Double topRightLong);

    List<Post> findByCreateDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
