package everycoding.nalseebackend.post.repository;

import everycoding.nalseebackend.post.repository.Post;
import everycoding.nalseebackend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Modifying
    @Transactional
    @Query("DELETE FROM Post p WHERE p.user = :user")
    void deleteByUser(User user);
}
