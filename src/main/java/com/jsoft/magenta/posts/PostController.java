package com.jsoft.magenta.posts;

import static com.jsoft.magenta.util.AppDefaults.ASCENDING_SORT;
import static com.jsoft.magenta.util.AppDefaults.DEFAULT_POST_SORT;
import static com.jsoft.magenta.util.AppDefaults.PAGE_INDEX;
import static com.jsoft.magenta.util.AppDefaults.PAGE_SIZE;

import com.jsoft.magenta.events.posts.PostReactiveEvent;
import com.jsoft.magenta.security.annotations.posts.PostWritePermission;
import com.jsoft.magenta.util.AppDefaults;
import com.jsoft.magenta.util.pagination.PageResponse;
import com.jsoft.magenta.util.validation.annotations.ValidContent;
import com.jsoft.magenta.util.validation.annotations.ValidTitle;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Validated
@RestController
@RequestMapping("${application.url}posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;
  private final PostsDelegationService delegationService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PostWritePermission
  public Post createPost(@RequestBody @Valid Post post) {
    return this.postService.createPost(post);
  }

  @PutMapping
  @PostWritePermission
  public Post updatePost(@RequestBody @Valid Post post) {
    return this.postService.updatePost(post);
  }

  @PatchMapping("title/{postId}")
  @PostWritePermission
  public Post updatePostTitle(
      @PathVariable Long postId,
      @RequestBody @ValidTitle String newTitle
  ) {
    return this.postService.updatePostTitle(postId, newTitle);
  }

  @PatchMapping("content/{postId}")
  @PostWritePermission
  public Post updatePostContent(
      @PathVariable Long postId,
      @RequestBody @ValidContent String newContent
  ) {
    return this.postService.updatePostContent(postId, newContent);
  }

  @GetMapping
  public PageResponse<Post> getAllPosts(
      @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
      @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
      @RequestParam(required = false, defaultValue = DEFAULT_POST_SORT) String sortBy,
      @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
  ) {
    return this.postService.getAllPosts(pageIndex, pageSize, sortBy, asc);
  }

  @GetMapping("{postId}")
  public Post getPost(@PathVariable Long postId) {
    return this.postService.getPost(postId);
  }

  @GetMapping("search")
  public List<PostSearchResult> getPostsResultsByTextExample(@RequestParam String textExample,
      @RequestParam(required = false, defaultValue = AppDefaults.RESULTS_COUNT) int resultsCount) {
    return this.postService.getAllPostsResultsByTextExample(textExample, resultsCount);
  }

  @GetMapping("newsfeed/{userId}")
  public Flux<PostReactiveEvent> listToNewsfeed(@PathVariable Long userId) {
    return Flux.create(emitter -> this.delegationService.delegateRequest(userId, emitter::next));
  }

  @DeleteMapping("{postId}")
  public void deletePost(@PathVariable Long postId) {
    this.postService.deletePost(postId);
  }

}
