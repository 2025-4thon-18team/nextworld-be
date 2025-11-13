package com.likelion.nextworld.domain.post.controller;

import com.likelion.nextworld.domain.post.dto.CommentRequest;
import com.likelion.nextworld.domain.post.dto.CommentResponse;
import com.likelion.nextworld.domain.post.service.CommentService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Tag(name = "Comment", description = "댓글 관련 API")
public class CommentController {

  private final CommentService commentService;

  @Operation(
      summary = "댓글 생성",
      description = """
          특정 게시글(postId)에 댓글을 생성합니다. (로그인 필요)
          """)
  @PostMapping("/{postId}/comments")
  public ResponseEntity<BaseResponse<CommentResponse>> createComment(
      @PathVariable Long postId,
      @AuthenticationPrincipal UserPrincipal user,
      @RequestBody CommentRequest.Create request) {

    CommentResponse res = commentService.create(postId, user, request);
    return ResponseEntity.ok(BaseResponse.success("댓글이 등록되었습니다.", res));
  }

  @Operation(
      summary = "댓글 수정",
      description = """
          특정 댓글(commentId)을 수정합니다. (로그인 필요)
          """)
  @PutMapping("/comments/{commentId}")
  public ResponseEntity<BaseResponse<CommentResponse>> updateComment(
      @PathVariable Long commentId,
      @AuthenticationPrincipal UserPrincipal user,
      @RequestBody CommentRequest.Update request) {

    CommentResponse res = commentService.update(commentId, user, request);
    return ResponseEntity.ok(BaseResponse.success("댓글이 수정되었습니다.", res));
  }

  @Operation(summary = "댓글 생성순 조회", description = "특정 게시글(postId)에 달린 댓글 목록을 생성순으로 조회합니다.")
  @GetMapping("/{postId}/comments")
  public ResponseEntity<BaseResponse<List<CommentResponse>>> getComments(
      @PathVariable Long postId) {

    List<CommentResponse> list = commentService.getListByPost(postId);
    return ResponseEntity.ok(BaseResponse.success("댓글 목록을 조회했습니다.", list));
  }

  @Operation(
      summary = "댓글 삭제",
      description =
          """
              특정 댓글(commentId)을 삭제합니다. (로그인 필요)
              - 자식 댓글이 있어도 해당 댓글만 삭제됩니다.
              """)
  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<BaseResponse<String>> deleteComment(
      @PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal user) {

    commentService.delete(commentId, user);
    return ResponseEntity.ok(BaseResponse.success("댓글이 삭제되었습니다."));
  }
}
