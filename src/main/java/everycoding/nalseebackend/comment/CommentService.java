package everycoding.nalseebackend.comment;

import everycoding.nalseebackend.comment.dto.CommentRequestDto;
import everycoding.nalseebackend.comment.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {

    List<CommentResponseDto> getComments(Long userId, Long postId);

    void writeComment(Long postId, CommentRequestDto requestDto);

    void updateComment(Long userId, Long postId, Long commentId, CommentRequestDto requestDto);

    void deleteComment(Long userId, Long postId, Long commentId);

    void likeComment(Long userId, Long postId, Long commentId);

    void cancelLikeComment(Long userId, Long postId, Long commentId);
}
