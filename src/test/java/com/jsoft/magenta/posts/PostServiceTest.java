package com.jsoft.magenta.posts;


import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.util.WordFormatter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

public class PostServiceTest
{
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    private static MockedStatic<UserEvaluator> mockedStatic;

    @BeforeAll
    private static void initStaticMock()
    {
        mockedStatic = mockStatic(UserEvaluator.class);
    }

    @BeforeEach
    private void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create post")
    public void createPost()
    {
        Post post = new Post();
        post.setTitle("title");
        post.setContent("content");
        post.setImage("image");
        Post returnedPost = new Post();
        returnedPost.setId(1L);
        post.setTitle("title");
        post.setContent("content");
        post.setImage("image");

        mockedStatic.when(UserEvaluator::currentUserName).thenReturn("name");
        Mockito.when(postRepository.save(post)).thenReturn(returnedPost);

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

        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Update post")
    public void updatePost()
    {
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

        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(postRepository.save(post)).thenReturn(returnedPost);

        Post savedPost = this.postService.updatePost(post);

        Assertions.assertThat(post).usingRecursiveComparison().isEqualTo(savedPost);

        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Update post title")
    public void updatePostTitle()
    {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("title");
        post.setContent("content");
        post.setImage("image");
        String newTitle = "new title";

        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(postRepository.save(post)).thenReturn(post);

        Post savedPost = this.postService.updatePostTitle(post.getId(), newTitle);

        Assertions.assertThat(savedPost).extracting("title").isEqualTo(WordFormatter.capitalizeFormat(newTitle));

        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Update post content")
    public void updatePostContent()
    {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("title");
        post.setContent("content");
        post.setImage("image");
        String newContent = "new content";

        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(postRepository.save(post)).thenReturn(post);

        Post savedPost = this.postService.updatePostContent(post.getId(), newContent);

        Assertions.assertThat(savedPost).extracting("content").isEqualTo(post.getContent());

        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Get all posts")
    public void getAllPosts()
    {
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        List<Post> postsList = List.of(new Post(), new Post());
        Page<Post> posts = new PageImpl<>(postsList, pageRequest, 2);

        Mockito.when(postRepository.findAll(pageRequest)).thenReturn(posts);

        Page<Post> results = this.postService.getAllPosts(0, 5, "title", true);

        Assertions.assertThat(results)
                .isNotEmpty()
                .hasSameSizeAs(posts)
                .containsAll(postsList);

        verify(postRepository).findAll(pageRequest);
    }

    @Test
    @DisplayName("Get post")
    public void getPost()
    {
        Post post = new Post();
        post.setId(1L);

        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        Post result = this.postService.getPost(post.getId());

        Assertions.assertThat(result).isEqualTo(post);

        verify(postRepository).findById(post.getId());
    }

    @Test
    @DisplayName("Delete post")
    public void deletePost()
    {
        Mockito.doNothing().when(postRepository).deleteById(1L);

        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));
        this.postService.deletePost(1L);

        verify(postRepository).deleteById(1L);
        verify(postRepository).findById(1L);
    }
}
