package com.jsoft.magenta.notes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNoteRepository extends JpaRepository<UserNote, Long> {

  Optional<UserNote> findByIdAndUserId(Long noteId, Long userId);

  Page<UserNote> findAllByUserId(Long userId, Pageable pageable);

  Page<UserNote> findAllByUserIdAndTakenAtLessThanEqual(Long userId, LocalDateTime maxDate,
      Pageable pageable);

  Page<UserNote> findAllByUserIdAndTakenAtGreaterThanEqual(Long userId, LocalDateTime minDate,
      Pageable pageable);

  Page<UserNote> findAllByUserIdAndTakenAtBetween(Long userId, LocalDateTime minDate,
      LocalDateTime maxDate,
      Pageable pageable);

  Page<UserNote> findAllByUserIdAndRemindAtLessThanEqual(Long userId, LocalDateTime maxDate,
      Pageable pageable);

  Page<UserNote> findAllByUserIdAndRemindAtGreaterThanEqual(Long userId, LocalDateTime minDate,
      Pageable pageable);

  Page<UserNote> findAllByUserIdAndRemindAtBetween(Long userId, LocalDateTime minDate,
      LocalDateTime maxDate,
      Pageable pageable);

  List<UserNoteSearchResult> findAllByUserIdAndTitleContainingIgnoreCase(Long userId, String title,
      Pageable pageable);
}
