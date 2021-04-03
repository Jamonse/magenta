package com.jsoft.magenta.posts;


import static org.mockito.Mockito.verify;

import com.jsoft.magenta.events.posts.PostReactiveEvent;
import com.jsoft.magenta.events.reactive.ReactiveEventType;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.util.WordFormatter;
import com.jsoft.magenta.util.pagination.PageResponse;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PostServiceTest {

  @InjectMocks
  private PostService postService;

  @Mock
  private SecurityService securityService;

  @Mock
  private PostRepository postRepository;

  @Mock
  private PostsDelegationService delegationService;

  @BeforeEach
  private void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create post")
  public void createPost() {
    Post post = new Post();
    post.setTitle("title");
    post.setContent("content");
    post.setImage("image");
    Post returnedPost = new Post();
    returnedPost.setId(1L);
    post.setTitle("title");
    post.setContent("content");
    post.setImage("image");

    PostReactiveEvent postReactiveEvent = new PostReactiveEvent(post, ReactiveEventType.CREATE);

    Mockito.when(securityService.currentUserName()).thenReturn("name");
    Mockito.when(postRepository.save(post)).thenReturn(returnedPost);
    Mockito.doNothing().when(delegationService).sendEvent(postReactiveEvent);

    this.postService.createPost(post);

    Assertions.assertThat(returnedPost)
        .extracting("id")
        .isNotNull();
    Assertions.assertThat(post)
        .extracting("createdAt")
        .isNotNull();
    Assertions.assertThat(post)
        .extracting("createdBy")
        .isNotNull()
        .isEqualTo("name");

    Mockito.verify(postRepository).save(post);
    Mockito.verify(delegationService).sendEvent(Mockito.any(PostReactiveEvent.class));
  }

  @Test
  @DisplayName("Update post")
  public void updatePost() {
    Post post = new Post();
    post.setId(1L);
    post.setTitle("title");
    post.setContent("content");
    post.setImage("image");
    Post returnedPost = new Post();
    returnedPost.setId(1L);
    returnedPost.setTitle("Title");
    returnedPost.setContent("content");
    returnedPost.setImage("image");

    PostReactiveEvent postReactiveEvent = new PostReactiveEvent(post, ReactiveEventType.UPDATE);
    Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
    Mockito.when(postRepository.save(post)).thenReturn(returnedPost);
    Mockito.doNothing().when(delegationService).sendEvent(postReactiveEvent);

    Post savedPost = this.postService.updatePost(post);

    Assertions.assertThat(post).usingRecursiveComparison().isEqualTo(savedPost);

    verify(postRepository).save(post);
  }

  @Test
  @DisplayName("Update post title")
  public void updatePostTitle() {
    Post post = new Post();
    post.setId(1L);
    post.setTitle("title");
    post.setContent("content");
    post.setImage("image");
    String newTitle = "new title";

    PostReactiveEvent postReactiveEvent = new PostReactiveEvent(post, ReactiveEventType.UPDATE);

    Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
    Mockito.when(postRepository.save(post)).thenReturn(post);
    Mockito.doNothing().when(delegationService).sendEvent(postReactiveEvent);

    Post savedPost = this.postService.updatePostTitle(post.getId(), newTitle);

    Assertions.assertThat(savedPost).extracting("title")
        .isEqualTo(WordFormatter.capitalizeFormat(newTitle));

    verify(postRepository).save(post);
  }

  @Test
  @DisplayName("Update post content")
  public void updatePostContent() {
    Post post = new Post();
    post.setId(1L);
    post.setTitle("title");
    post.setContent("content");
    post.setImage("image");
    String newContent = "new content";

    PostReactiveEvent postReactiveEvent = new PostReactiveEvent(post, ReactiveEventType.UPDATE);

    Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
    Mockito.when(postRepository.save(post)).thenReturn(post);
    Mockito.doNothing().when(delegationService).sendEvent(postReactiveEvent);

    Post savedPost = this.postService.updatePostContent(post.getId(), newContent);

    Assertions.assertThat(savedPost).extracting("content").isEqualTo(post.getContent());

    verify(postRepository).save(post);
  }

  @Test
  @DisplayName("Get all posts")
  public void getAllPosts() {
    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    List<Post> postsList = List.of(new Post(), new Post());
    Page<Post> postsResult = new PageImpl<>(postsList, pageRequest, 2);
    PageResponse<Post> posts = new PageResponse<>(postsList, postsList.size(), pageRequest);

    Mockito.when(postRepository.findAll(pageRequest)).thenReturn(postsResult);

    PageResponse<Post> results = this.postService.getAllPosts(0, 5, "title", true);

    Assertions.assertThat(results)
        .extracting("content")
        .isNotNull();

    verify(postRepository).findAll(pageRequest);
  }

  @Test
  @DisplayName("Get post")
  public void getPost() {
    Post post = new Post();
    post.setId(1L);

    Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

    Post result = this.postService.getPost(post.getId());

    Assertions.assertThat(result).isEqualTo(post);

    verify(postRepository).findById(post.getId());
  }

  @Test
  @DisplayName("Delete post")
  public void deletePost() {

    Post post = new Post();
    post.setId(1L);

    PostReactiveEvent postReactiveEvent = new PostReactiveEvent(post, ReactiveEventType.DELETE);
    Mockito.doNothing().when(delegationService).sendEvent(postReactiveEvent);
    Mockito.doNothing().when(postRepository).deleteById(1L);
    Mockito.when(postRepository.existsById(1L)).thenReturn(true);

    this.postService.deletePost(1L);

    verify(postRepository).deleteById(1L);
    verify(postRepository).existsById(1L);
  }
}
