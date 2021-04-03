package com.jsoft.magenta.notes;

import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.ReminderException;
import com.jsoft.magenta.exceptions.UpdateViolationException;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.util.pagination.PageRequestBuilder;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserNoteService {

  private final UserNoteRepository userNoteRepository;
  private final SecurityService securityService;

  public UserNote createUserNote(UserNote userNote) {
    verifyNoteRemindAt(userNote.getRemindAt());
    userNote.setTakenAt(LocalDateTime.now());
    userNote.setUser(securityService.currentUser());
    return this.userNoteRepository.save(userNote);
  }

  public UserNote updateNoteTitle(Long noteId, String newTitle) {
    UserNote userNote = findNote(noteId);
    userNote.setTitle(newTitle);
    return this.userNoteRepository.save(userNote);
  }

  public UserNote updateNoteContent(Long noteId, String newNoteContent) {
    UserNote userNote = findNote(noteId);
    userNote.setContent(newNoteContent);
    return this.userNoteRepository.save(userNote);
  }

  public UserNote updateUserNoteRemindTime(Long noteId, LocalDateTime newRemindTime) {
    UserNote userNote = findNote(noteId);
    verifyNoteRemindAt(newRemindTime);
    userNote.setRemindAt(newRemindTime);
    return this.userNoteRepository.save(userNote);
  }

  public UserNote updateUserNote(UserNote userNote) {
    UserNote userNoteToUpdate = findNote(userNote.getId());
    if (userNote.getTakenAt() != userNoteToUpdate.getTakenAt()) {
      throw new UpdateViolationException("Cannot modify note creation time");
    }
    userNoteToUpdate.setTitle(userNote.getTitle());
    userNoteToUpdate.setContent(userNote.getContent());
    userNoteToUpdate.setRemindAt(userNote.getRemindAt());
    return this.userNoteRepository.save(userNoteToUpdate);
  }

  public Page<UserNote> getAllUserNotes(
      int pageIndex, int pageSize, String sortBy, boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Long userId = securityService.currentUserId();
    Page<UserNote> pageResult = this.userNoteRepository.findAllByUserId(userId, pageRequest);
    return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
  }

  public Page<UserNote> getAllUserNotesTakenBefore(
      LocalDateTime maxDate, int pageIndex, int pageSize, String sortBy, boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Long userId = securityService.currentUserId();
    Page<UserNote> pageResult = this.userNoteRepository
        .findAllByUserIdAndTakenAtLessThanEqual(userId, maxDate, pageRequest);
    return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
  }

  public Page<UserNote> getAllUserNotesTakenAfter(
      LocalDateTime minDate, int pageIndex, int pageSize, String sortBy, boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Long userId = securityService.currentUserId();
    Page<UserNote> pageResult = this.userNoteRepository
        .findAllByUserIdAndTakenAtGreaterThanEqual(userId, minDate, pageRequest);
    return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
  }

  public Page<UserNote> getAllUserNotesTakenBetween(
      LocalDateTime minDate, LocalDateTime maxDate, int pageIndex, int pageSize, String sortBy,
      boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Long userId = securityService.currentUserId();
    Page<UserNote> pageResult = this.userNoteRepository
        .findAllByUserIdAndTakenAtBetween(userId, minDate, maxDate, pageRequest);
    return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
  }

  public Page<UserNote> getAllUserNotesRemindBefore(
      LocalDateTime maxDate, int pageIndex, int pageSize, String sortBy, boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Long userId = securityService.currentUserId();
    Page<UserNote> pageResult = this.userNoteRepository
        .findAllByUserIdAndRemindAtLessThanEqual(userId, maxDate, pageRequest);
    return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
  }

  public Page<UserNote> getAllUserNotesRemindAfter(
      LocalDateTime minDate, int pageIndex, int pageSize, String sortBy, boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Long userId = securityService.currentUserId();
    Page<UserNote> pageResult = this.userNoteRepository
        .findAllByUserIdAndRemindAtGreaterThanEqual(userId, minDate, pageRequest);
    return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
  }

  public Page<UserNote> getAllUserNotesRemindBetween(
      LocalDateTime minDate, LocalDateTime maxDate, int pageIndex, int pageSize, String sortBy,
      boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Long userId = securityService.currentUserId();
    Page<UserNote> pageResult = this.userNoteRepository
        .findAllByUserIdAndRemindAtBetween(userId, minDate, maxDate, pageRequest);
    return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
  }

  public List<UserNoteSearchResult> getAllUserNotesByTitleExample(
      String titleExample, int pageIndex, int pageSize, String sortBy, boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Long userId = securityService.currentUserId();
    return this.userNoteRepository
        .findAllByUserIdAndTitleContainingIgnoreCase(userId, titleExample, pageRequest);
  }

  public void deleteNote(Long noteId) {
    isNoteExists(noteId);
    this.userNoteRepository.deleteById(noteId);
  }

  private UserNote findNote(Long noteId) {
    Long userId = securityService.currentUserId();
    return this.userNoteRepository
        .findByIdAndUserId(noteId, userId)
        .orElseThrow(() -> new NoSuchElementException("Note not found for user"));
  }

  private void verifyNoteRemindAt(LocalDateTime noteRemindAt) {
    if (noteRemindAt != null && noteRemindAt.isBefore(LocalDateTime.now().plusMinutes(1))) {
      throw new ReminderException("Remind time must be at least one minute from current time");
    }
  }

  private void isNoteExists(Long noteId) {
    boolean exists = this.userNoteRepository.existsById(noteId);
    if (!exists) {
      throw new NoSuchElementException("Note not found for user");
    }
  }

}
