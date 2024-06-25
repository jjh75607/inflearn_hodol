package com.hodol.api.controller;

import com.hodol.api.request.PostCreate;
import com.hodol.api.request.PostSearch;
import com.hodol.api.response.PostResponse;
import com.hodol.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public void post(@RequestBody @Valid PostCreate request) {
        postService.write(request);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getAll(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }
}