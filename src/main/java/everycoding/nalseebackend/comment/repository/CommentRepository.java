package everycoding.nalseebackend.comment.repository;

import everycoding.nalseebackend.post.repository.Post;
import everycoding.nalseebackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.user = :user")
    void deleteByUser(User user);
}
