package com.buixuanphat.spot_on.controller;

import com.buixuanphat.spot_on.dto.ApiResponse;
import com.buixuanphat.spot_on.dto.post.CreatePostDTO;
import com.buixuanphat.spot_on.dto.post.PostResponseDTO;
import com.buixuanphat.spot_on.service.PostService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {

    PostService postService;

    @NonFinal
    @Value("${pagination.page-size}")
    int size;


    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponseDTO> createPost(@ModelAttribute CreatePostDTO request, @Nullable @RequestParam("images") List<MultipartFile> images) {
        return ApiResponse.<PostResponseDTO>builder().data(postService.create(request, images)).build();
    }



    @GetMapping("/posts")
    ApiResponse<Page<PostResponseDTO>> getPosts(@RequestParam @Nullable String kw , @RequestParam @Nullable String time, @RequestParam @Nullable Integer userId , @RequestParam(defaultValue = "0") int page)
    {
        return ApiResponse.<Page<PostResponseDTO>>builder().data(postService.getPosts(kw , userId ,page, size)).build();
    }


    @GetMapping("/posts/{id}")
    ApiResponse<PostResponseDTO> getPost(@PathVariable int id)
    {
        return ApiResponse.<PostResponseDTO>builder().data(postService.getPost(id)).build();
    }


    @DeleteMapping("/posts/{id}")
    ApiResponse<String> deletePost(@PathVariable int id)
    {
        return ApiResponse.<String>builder().data(postService.deletePost(id)).build();
    }

}
