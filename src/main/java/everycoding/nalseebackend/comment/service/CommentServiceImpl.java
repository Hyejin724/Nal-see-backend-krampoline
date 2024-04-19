package everycoding.nalseebackend.comment.service;

import everycoding.nalseebackend.api.exception.BaseException;
import everycoding.nalseebackend.comment.domain.Comment;
import everycoding.nalseebackend.comment.controller.dto.CommentRequestDto;
import everycoding.nalseebackend.comment.repository.CommentRepository;
import everycoding.nalseebackend.comment.service.info.CommentInfo;
import everycoding.nalseebackend.post.repository.PostRepository;
import everycoding.nalseebackend.post.domain.Post;
import everycoding.nalseebackend.user.repository.UserRepository;
import everycoding.nalseebackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentInfo> getComments(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException("wrong postId"));
        return commentRepository.findAllByPost(post)
                .stream()
                .map(comment -> CommentInfo.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .likeCNT(comment.getLikeCNT())
                        .isLiked(isLiked(userId, comment.getId()))
                        .createDate(comment.getCreateDate())
                        .userId(comment.getUser().getId())
                        .userImage(comment.getUser().getPicture())
                        .username(comment.getUser().getUsername())
                        .postId(comment.getPost().getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void writeComment(Long postId, CommentRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(() -> new BaseException("wrong userId"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException("wrong postId"));

        commentRepository.save(
                Comment.builder()
                        .content(requestDto.getContent())
                        .user(user)
                        .post(post)
                        .build());
    }

    @Override
    public void updateComment(Long userId, Long postId, Long commentId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BaseException("wrong commentId"));
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        if (!comment.getUser().equals(user)) {
            throw new BaseException("수정할 수 있는 권한이 없습니다.");
        }

        comment.setContent(requestDto.getContent());
    }

    @Override
    public void deleteComment(Long userId, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BaseException("wrong commentId"));
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        if (!comment.getUser().equals(user)) {
            throw new BaseException("삭제할 수 있는 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    @Override
    public void likeComment(Long userId, Long postId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BaseException("wrong commentId"));

        user.addCommentLike(commentId);
        comment.increaseLikeCNT();
    }

    @Override
    public void cancelLikeComment(Long userId, Long postId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BaseException("wrong commentId"));

        user.cancelCommentLike(commentId);
        comment.decreaseLikeCNT();
    }

    private boolean isLiked(Long userId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        return user.getCommentLikeList().contains(commentId);
    }
}
