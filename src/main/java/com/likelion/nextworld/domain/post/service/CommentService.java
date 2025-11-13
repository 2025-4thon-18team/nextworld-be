package com.likelion.nextworld.domain.post.service;

import com.likelion.nextworld.domain.post.dto.CommentRequest;
import com.likelion.nextworld.domain.post.dto.CommentResponse;
import com.likelion.nextworld.domain.post.entity.Comment;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.exception.CommentErrorCode;
import com.likelion.nextworld.domain.post.exception.WorkErrorCode;
import com.likelion.nextworld.domain.post.mapper.CommentMapper;
import com.likelion.nextworld.domain.post.repository.CommentRepository;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.exception.UserErrorCode;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final CommentMapper commentMapper;

  private User getCurrentUser(UserPrincipal principal) {
    if (principal == null || principal.getId() == null) {
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }
    return userRepository
        .findById(principal.getId())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  /**
   * 댓글/대댓글 생성
   */
  public CommentResponse create(Long postId, UserPrincipal principal, CommentRequest.Create req) {
    User user = getCurrentUser(principal);

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new CustomException(WorkErrorCode.WORK_NOT_FOUND));

    Comment parent = null;
    if (req.getParentCommentId() != null) {
      parent =
          commentRepository
              .findById(req.getParentCommentId())
              .orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));
      // 부모 댓글이 같은 게시글에 속하는지 검증
      if (!parent.getPost().getId().equals(postId)) {
        throw new CustomException(CommentErrorCode.COMMENT_FORBIDDEN);
      }
    }

    Comment toSave =
        Comment.builder().post(post).author(user).parent(parent).content(req.getContent()).build();

    Comment saved = commentRepository.save(toSave);
    return commentMapper.toResponse(saved);
  }

  /**
   * 댓글 수정 (commentId 기준)
   */
  public CommentResponse update(
      Long commentId, UserPrincipal principal, CommentRequest.Update req) {
    User user = getCurrentUser(principal);

    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));

    // 작성자 검증
    if (!comment.getAuthor().getUserId().equals(user.getUserId())) {
      throw new CustomException(CommentErrorCode.COMMENT_FORBIDDEN);
    }

    comment.updateContent(req.getContent());
    return commentMapper.toResponse(comment);
  }

  /**
   * 댓글 조회 (최신순)
   */
  @Transactional(readOnly = true)
  public List<CommentResponse> getListByPost(Long postId) {
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new CustomException(WorkErrorCode.WORK_NOT_FOUND));

    List<Comment> comments = commentRepository.findAllByPostOrderByIdAsc(post);
    return commentMapper.toResponseList(comments);
  }

  /**
   * 댓글 삭제 (자식 댓글은 유지, 해당 댓글만 삭제)
   */
  public void delete(Long commentId, UserPrincipal principal) {
    User user = getCurrentUser(principal);

    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));

    // 작성자 검증
    if (!comment.getAuthor().getUserId().equals(user.getUserId())) {
      throw new CustomException(CommentErrorCode.COMMENT_FORBIDDEN);
    }

    commentRepository.delete(comment);
  }
}
