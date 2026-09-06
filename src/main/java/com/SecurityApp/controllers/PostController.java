package com.SecurityApp.controllers;

import com.SecurityApp.dto.PostDto;
import com.SecurityApp.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")

public class PostController {

    private final PostService postService;
    public PostController(PostService postService){
        this.postService=postService;
    }

    @GetMapping
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{postId}")
    public  PostDto getPostById(@PathVariable Long postId){
        return postService.getPostById(postId);
    }
    @PostMapping
    public PostDto createNewPosts(@RequestBody PostDto postDto) {
        return postService.createNewPost(postDto);
    }
    @PutMapping("/{postId}")
    public PostDto updatePost(
            @RequestBody PostDto postDto,
            @PathVariable Long postId) {

        return postService.updatePost(postDto, postId);
    }
}