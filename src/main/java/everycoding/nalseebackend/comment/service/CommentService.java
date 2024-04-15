package everycoding.nalseebackend.comment.service;

import everycoding.nalseebackend.comment.controller.dto.CommentRequestDto;
import everycoding.nalseebackend.comment.controller.dto.CommentResponseDto;
import everycoding.nalseebackend.comment.service.info.CommentInfo;

import java.util.List;

public interface CommentService {

    List<CommentInfo> getComments(Long userId, Long postId);

    void writeComment(Long postId, CommentRequestDto requestDto);

    void updateComment(Long userId, Long postId, Long commentId, CommentRequestDto requestDto);

    void deleteComment(Long userId, Long postId, Long commentId);

    void likeComment(Long userId, Long postId, Long commentId);

    void cancelLikeComment(Long userId, Long postId, Long commentId);
}
