package com.jsoft.magenta.posts;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<PostSearchResult> findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
      String titleExample, String contentExample, Pageable pageable);

  default List<PostSearchResult> findPostsByExample(String textExample, Pageable pageable) {
    return this
        .findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(textExample, textExample,
            pageable);
  }
}
