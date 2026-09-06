package com.SecurityApp.respositories;

import com.SecurityApp.dto.PostDto;
import com.SecurityApp.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,Long> {
}
