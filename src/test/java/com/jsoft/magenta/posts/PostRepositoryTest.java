package com.jsoft.magenta.posts;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Test
  @DisplayName("Save post")
  public void savePost() {
    Post post = new Post();
    post.setTitle("title");
    post.setContent("content");
    post.setCreatedBy("user");
    post.setCreatedAt(LocalDateTime.now());
    post.setImage("image");

    Post savedPost = this.postRepository.save(post);

    Assertions.assertThat(savedPost)
        .extracting("id")
        .isNotNull();
    Assertions.assertThat(savedPost).usingRecursiveComparison()
        .ignoringFields("id").isEqualTo(post);
  }
}
