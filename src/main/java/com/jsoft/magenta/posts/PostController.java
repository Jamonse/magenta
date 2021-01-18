package com.jsoft.magenta.posts;

import com.jsoft.magenta.security.annotations.posts.PostWritePermission;
import com.jsoft.magenta.util.validation.ValidContent;
import com.jsoft.magenta.util.validation.ValidTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.jsoft.magenta.util.AppDefaults.*;

@Validated
@RestController
@RequestMapping("${application.url}posts")
@RequiredArgsConstructor
public class PostController
{
    private final PostService postService;

    public static final String DEFAULT_POST_SORT = "title";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PostWritePermission
    public Post createPost(@RequestBody @Valid Post post)
    {
        return this.postService.createPost(post);
    }

    @PutMapping
    @PostWritePermission
    public Post updatePost(@RequestBody @Valid Post post)
    {
        return this.postService.updatePost(post);
    }

    @PatchMapping("title/{postId}")
    @PostWritePermission
    public Post updatePostTitle(
            @PathVariable Long postId,
            @RequestBody @ValidTitle String newTitle
    )
    {
        return this.postService.updatePostTitle(postId, newTitle);
    }

    @PatchMapping("content/{postId}")
    @PostWritePermission
    public Post updatePostContent(
            @PathVariable Long postId,
            @RequestBody @ValidContent String newContent
    )
    {
        return this.postService.updatePostContent(postId, newContent);
    }

    @GetMapping
    public Page<Post> getAllPosts(
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_POST_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.postService.getAllPosts(pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("{postId}")
    public Post getPost(@PathVariable Long postId)
    {
        return this.postService.getPost(postId);
    }

    @DeleteMapping("{postId}")
    public void deletePost(@PathVariable Long postId)
    {
        this.postService.deletePost(postId);
    }

}
