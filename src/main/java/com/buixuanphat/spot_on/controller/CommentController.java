package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.comment.CommentResponseDTO;
import com.buixuanphat.spot_on.dto.comment.CreateCommentDTO;
import com.buixuanphat.spot_on.service.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @NonFinal
    @Value("${pagination.page-size}")
    int size;


    @PostMapping("/comments")
    ApiResponse<CommentResponseDTO> create (@Valid @RequestBody CreateCommentDTO request){
        return ApiResponse.<CommentResponseDTO>builder().data(commentService.create(request)).build();
    }


    @GetMapping("/comments/parents")
    ApiResponse<Page<CommentResponseDTO>> getParentComments (@RequestParam int postId, @RequestParam(defaultValue = "0") int page){
        return ApiResponse.<Page<CommentResponseDTO>>builder().data(commentService.getParentComments(postId, page, size)).build();
    }

    @GetMapping("/comments/children")
    ApiResponse<Page<CommentResponseDTO>> getChildrenComments (@RequestParam int parentId, @RequestParam(defaultValue = "0") int page){
        return ApiResponse.<Page<CommentResponseDTO>>builder().data(commentService.getChildComments(parentId, page, size)).build();
    }


    @GetMapping("/comments/amount/{postId}")
    ApiResponse<Integer> getNumberOfComment (@PathVariable int postId){
        return ApiResponse.<Integer>builder().data(commentService.getNumberOfComment(postId)).build();
    }
}
