package com.jsoft.magenta.posts;

import com.jsoft.magenta.events.posts.PostReactiveEvent;
import com.jsoft.magenta.events.reactive.ReactiveEventType;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.util.PageRequestBuilder;
import com.jsoft.magenta.util.WordFormatter;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final SecurityService securityService;
  private final PostsDelegationService delegationService;

  public Post createPost(Post post) {
    // Set post date according to user and post time
    String userName = securityService.currentUserName();
    post.setTitle(WordFormatter.capitalizeFormat(post.getTitle()));
    post.setCreatedAt(LocalDateTime.now());
    post.setCreatedBy(userName);
    // Save post
    Post savedPost = this.postRepository.save(post);
    // Update newsfeed with new post
    this.delegationService
        .sendEvent(new PostReactiveEvent(savedPost, ReactiveEventType.CREATE));
    // Return saved post after updating
    return savedPost;
  }

  public Post updatePost(Post post) {
    Post postToUpdate = findPost(post.getId());
    postToUpdate.setTitle(WordFormatter.capitalizeFormat(post.getTitle()));
    postToUpdate.setContent(post.getContent());
    postToUpdate.setImage(post.getImage());
    Post savedPost = this.postRepository.save(postToUpdate);
    this.delegationService
        .sendEvent(new PostReactiveEvent(savedPost, ReactiveEventType.UPDATE));
    return savedPost;
  }

  public Post updatePostTitle(Long postId, String newTitle) {
    Post post = findPost(postId);
    post.setTitle(WordFormatter.capitalizeFormat(newTitle));
    Post savedPost = this.postRepository.save(post);
    this.delegationService
        .sendEvent(new PostReactiveEvent(savedPost, ReactiveEventType.UPDATE));
    return savedPost;
  }

  public Post updatePostContent(Long postId, String newContent) {
    Post post = findPost(postId);
    post.setContent(newContent);
    Post savedPost = this.postRepository.save(post);
    this.delegationService
        .sendEvent(new PostReactiveEvent(savedPost, ReactiveEventType.UPDATE));
    return savedPost;
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
    Post post = findPost(postId);
    this.delegationService
        .sendEvent(new PostReactiveEvent(post, ReactiveEventType.DELETE));
    this.postRepository.deleteById(postId);
  }

  private Post findPost(Long postId) {
    return this.postRepository
        .findById(postId)
        .orElseThrow(() -> new NoSuchElementException("Post not found"));
  }

}
