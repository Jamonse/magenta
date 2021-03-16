package com.jsoft.magenta.posts;

import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.util.PageRequestBuilder;
import com.jsoft.magenta.util.WordFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final SecurityService securityService;

    public Post createPost(Post post) {
        String userName = securityService.currentUserName();
        post.setTitle(WordFormatter.capitalizeFormat(post.getTitle()));
        post.setCreatedAt(LocalDateTime.now());
        post.setCreatedBy(userName);
        return this.postRepository.save(post);
    }

    public Post updatePost(Post post) {
        Post postToUpdate = findPost(post.getId());
        postToUpdate.setTitle(WordFormatter.capitalizeFormat(post.getTitle()));
        postToUpdate.setContent(post.getContent());
        postToUpdate.setImage(post.getImage());
        return this.postRepository.save(post);
    }

    public Post updatePostTitle(Long postId, String newTitle) {
        Post post = findPost(postId);
        post.setTitle(WordFormatter.capitalizeFormat(newTitle));
        return this.postRepository.save(post);
    }

    public Post updatePostContent(Long postId, String newContent) {
        Post post = findPost(postId);
        post.setContent(newContent);
        return this.postRepository.save(post);
    }

    public Page<Post> getAllPosts(int pageIndex, int pageSize, String sortBy, boolean asc) {
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Post> pageResult = this.postRepository.findAll(pageRequest);
        return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
    }

    public Post getPost(Long postId) {
        return findPost(postId);
    }

    public void deletePost(Long postId) {
        isPostExists(postId);
        this.postRepository.deleteById(postId);
    }

    private Post findPost(Long postId) {
        return this.postRepository
                .findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
    }

    private void isPostExists(Long postId) {
        boolean exists = this.postRepository.existsById(postId);
        if (!exists)
            throw new NoSuchElementException("Post not found");
    }

}
