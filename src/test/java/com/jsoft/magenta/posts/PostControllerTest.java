package com.jsoft.magenta.posts;

import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.Stringify;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.FluxSink;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class PostControllerTest {

  @MockBean
  private PostService postService;

  @MockBean
  private PostsDelegationService delegationService;

  @Autowired
  private PostController postController;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("Post creation tests")
  @WithUserDetails("admin@admin.com")
  class PostCreationTests {

    @Test
    @DisplayName("Create post")
    public void createPost() throws Exception {
      Post post = new Post();
      post.setTitle("title");
      post.setContent("content");
      post.setImage("image");
      Post savedPost = new Post();
      savedPost.setId(1L);
      savedPost.setTitle("title");
      savedPost.setContent("content");
      savedPost.setImage("image");
      savedPost.setCreatedAt(LocalDateTime.now());
      savedPost.setCreatedBy("user");

      Mockito.when(postService.createPost(post)).thenReturn(savedPost);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "posts")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(post)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isCreated())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
          .andExpect(MockMvcResultMatchers.jsonPath("$.createdBy").value("user"));

      Mockito.verify(postService).createPost(post);
    }

    @Test
    @DisplayName("Create post with invalid title - should return 400")
    public void createPostWithInvalidTitle() throws Exception {
      Post post = new Post();
      post.setTitle("t");
      post.setContent("content");
      post.setImage("image");

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "posts")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(post)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers
              .jsonPath("$.errors[0]")
              .value(AppConstants.TITLE_LENGTH_MESSAGE));

      Mockito.verifyNoInteractions(postService);
    }

    @Test
    @DisplayName("Create post with invalid content - should return 400")
    public void createPostWithInvalidContent() throws Exception {
      Post post = new Post();
      post.setTitle("title");
      post.setImage("image");

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "posts")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(post)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers
              .jsonPath("$.errors[0]")
              .value(AppConstants.CONTENT_NULL_MESSAGE));

      Mockito.verifyNoInteractions(postService);
    }
  }

  @Nested
  @DisplayName("Post update tests")
  @WithUserDetails("admin@admin.com")
  class PostUpdateTests {

    @Test
    @DisplayName("Update post")
    public void updatePost() throws Exception {
      Post post = new Post();
      post.setId(1L);
      post.setTitle("title");
      post.setContent("content");
      post.setImage("image");
      post.setCreatedBy("user");
      Post updatedPost = new Post();
      updatedPost.setId(1L);
      updatedPost.setTitle("title");
      updatedPost.setContent("content");
      updatedPost.setImage("image");
      updatedPost.setCreatedAt(LocalDateTime.now());
      updatedPost.setCreatedBy("user");

      Mockito.when(postService.updatePost(post)).thenReturn(updatedPost);

      mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "posts")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(post)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(updatedPost.getId().toString()));

      Mockito.verify(postService).updatePost(post);
    }

    @Test
    @DisplayName("Update post title")
    public void updatePostTitle() throws Exception {
      Post post = new Post();
      post.setId(1L);
      post.setTitle("title");
      post.setContent("content");
      String newTitle = "title";

      Mockito.when(postService.updatePostTitle(post.getId(), "title")).thenReturn(post);

      mockMvc.perform(
          MockMvcRequestBuilders.patch(Stringify.BASE_URL + "posts/title/{postId}", post.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(newTitle))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(newTitle));

      Mockito.verify(postService).updatePostTitle(post.getId(), newTitle);
    }

    @Test
    @DisplayName("Update post content")
    public void updatePostContent() throws Exception {
      Post post = new Post();
      post.setId(1L);
      post.setTitle("title");
      post.setContent("content");
      String newContent = "content";

      Mockito.when(postService.updatePostContent(post.getId(), newContent))
          .thenReturn(post);

      mockMvc.perform(
          MockMvcRequestBuilders.patch(Stringify.BASE_URL + "posts/content/{postId}", post.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(newContent))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(newContent));

      Mockito.verify(postService).updatePostContent(post.getId(), newContent);
    }

    @Test
    @DisplayName("Update post with invalid title - should return 400")
    public void updatePostWithInvalidTitle() throws Exception {
      Post post = new Post();
      post.setId(1L);
      post.setTitle("title");
      post.setContent("content");
      String invalidTitle = "t";

      mockMvc.perform(
          MockMvcRequestBuilders.patch(Stringify.BASE_URL + "posts/title/{postId}", post.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(invalidTitle))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers
              .jsonPath("$.errors[0]")
              .value(AppConstants.TITLE_LENGTH_MESSAGE));

      Mockito.verifyNoInteractions(postService);
    }
  }

  @Nested
  @DisplayName("Post get tests")
  @WithUserDetails("admin@admin.com")
  class PostGetTests {

    @Test
    @DisplayName("Get all posts")
    public void getAllPosts() throws Exception {
      Sort sort = Sort.by("title").ascending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);
      List<Post> posts = List.of(new Post(), new Post());
      Page<Post> results = new PageImpl<>(posts, pageRequest, posts.size());

      Mockito.when(postService.getAllPosts(0, 5, "title", true))
          .thenReturn(results);

      mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "posts")
          .queryParam("pageIndex", "0")
          .queryParam("pageSize", "5")
          .queryParam("sortBy", "title")
          .queryParam("asc", "true"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
          .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());

      Mockito.verify(postService).getAllPosts(0, 5, "title", true);
    }

    @Test
    @DisplayName("Get post")
    public void getPost() throws Exception {
      Post post = new Post();
      post.setId(1L);
      post.setTitle("title");
      post.setContent("content");

      Mockito.when(postService.getPost(post.getId())).thenReturn(post);

      mockMvc
          .perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "posts/{postId}", post.getId()))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.content().json(Stringify.asJsonString(post)));

      Mockito.verify(postService).getPost(post.getId());
    }

    @Test
    @DisplayName("Listen to posts sse newsfeed endpoint")
    public void listen() throws Exception {

      Mockito.doNothing().when(delegationService).delegateRequest(Mockito.any(), Mockito.any());

      mockMvc
          .perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "posts/newsfeed/{requesterId}", 1L))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk());

      Mockito.verify(delegationService).delegateRequest(Mockito.any(), Mockito.any());
    }
  }

  @Test
  @DisplayName("Delete post")
  public void deletePost() throws Exception {
    Post post = new Post();
    post.setId(1L);
    post.setTitle("title");
    post.setContent("content");

    Mockito.doNothing().when(postService).deletePost(post.getId());

    mockMvc
        .perform(MockMvcRequestBuilders.delete(Stringify.BASE_URL + "posts/{postId}", post.getId()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());

    Mockito.verify(postService).deletePost(post.getId());
  }
}
