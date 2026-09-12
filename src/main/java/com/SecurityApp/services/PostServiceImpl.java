package com.SecurityApp.services;

import com.SecurityApp.dto.PostDto;
import com.SecurityApp.entities.PostEntity;
import com.SecurityApp.entities.User;
import com.SecurityApp.exceptions.ResourceNotFoundException;
import com.SecurityApp.respositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor

public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PostDto> getAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(postEntity -> modelMapper.map(postEntity, PostDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PostDto createNewPost(PostDto inputPost) {
        PostEntity postEntity = modelMapper.map(inputPost, PostEntity.class);
        PostEntity savedPostEntity = postRepository.save(postEntity);
        return modelMapper.map(savedPostEntity, PostDto.class);
    }

    @Override
    public PostDto getPostById(Long postId) {
//        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        log.info("user {}",user);
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return modelMapper.map(postEntity, PostDto.class);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long postId) {
        PostEntity olderPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        postDto.setId(postId);
        modelMapper.map(postDto, olderPost);
        PostEntity savedPostEntity = postRepository.save(olderPost);
        return modelMapper.map(savedPostEntity, PostDto.class);
    }
}
