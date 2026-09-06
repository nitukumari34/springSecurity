package com.SecurityApp.services;

import com.SecurityApp.dto.PostDto;
import java.util.List;

public interface PostService {
    List<PostDto> getAllPosts();
    PostDto createNewPost(PostDto inputPost);

    PostDto getPostById(Long postId);

    PostDto updatePost(PostDto postDto, Long postId);
}
