package com.jsoft.magenta.notes;

import com.jsoft.magenta.exceptions.ReminderException;
import com.jsoft.magenta.exceptions.TextLengthException;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.users.User;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class NoteServiceTest
{
    @InjectMocks
    private UserNoteService userNoteService;

    @Mock
    private UserNoteRepository userNoteRepository;

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

    @AfterAll
    private static void afterTesting()
    {
        mockedStatic.verify(times(1), UserEvaluator::currentUser);
        mockedStatic.verify(times(8), UserEvaluator::currentUserId);
        mockedStatic.close();
    }

    @Test
    @DisplayName("Save user note")
    public void saveUserNote()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        userNote.setContent("content");
        userNote.setTakenAt(LocalDateTime.now());
        UserNote returnedNote = new UserNote();
        returnedNote.setTitle(userNote.getTitle());
        returnedNote.setId(1L);

        when(userNoteRepository.save(userNote)).thenReturn(returnedNote);
        mockedStatic.when(UserEvaluator::currentUser).thenReturn(new User());

        UserNote note = this.userNoteService.createUserNote(userNote);

        Assertions.assertEquals(note.getTitle(), returnedNote.getTitle());
        verify(userNoteRepository).save(userNote);
    }

    @Test
    @DisplayName("Update user note reminder")
    public void updateUserNoteReminder()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        userNote.setRemindAt(LocalDateTime.now().plusDays(1));
        UserNote returnedNote = new UserNote();
        returnedNote.setTitle(userNote.getTitle());
        returnedNote.setRemindAt(LocalDateTime.now().plusDays(2));

        when(userNoteRepository.findById(1L)).thenReturn(Optional.of(userNote));
        when(userNoteRepository.save(userNote)).thenReturn(returnedNote);
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        UserNote note = this.userNoteService.updateUserNoteRemindTime(1L, LocalDateTime.now().plusDays(2));

        Assertions.assertEquals(note.getTitle(), returnedNote.getTitle());
        verify(userNoteRepository).save(userNote);
    }

    @Test
    @DisplayName("Update user note reminder with invalid date - should throw ReminderException")
    public void updateUserNoteReminderWithInvalidRemindAt()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        userNote.setRemindAt(LocalDateTime.now().plusDays(1));
        UserNote returnedNote = new UserNote();
        returnedNote.setTitle(userNote.getTitle());
        returnedNote.setRemindAt(LocalDateTime.now());

        when(userNoteRepository.findById(1L)).thenReturn(Optional.of(userNote));
        when(userNoteRepository.save(userNote)).thenReturn(returnedNote);
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Assertions.assertThrows(ReminderException.class,
                () -> this.userNoteService.updateUserNoteRemindTime(1L, LocalDateTime.now()));

        verify(userNoteRepository).findById(1L);
        verify(userNoteRepository, times(0)).save(userNote);
    }

    @Test
    @DisplayName("Update user note title with invalid title - should throw TextLengthException")
    public void updateUserNoteTitleWithInvalidTitle()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        userNote.setRemindAt(LocalDateTime.now().plusDays(1));
        UserNote returnedNote = new UserNote();
        returnedNote.setTitle("");
        returnedNote.setRemindAt(LocalDateTime.now());

        when(userNoteRepository.findById(1L)).thenReturn(Optional.of(userNote));
        when(userNoteRepository.save(userNote)).thenReturn(returnedNote);
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Assertions.assertThrows(TextLengthException.class,
                () -> this.userNoteService.updateNoteTitle(1L, returnedNote.getTitle()));

        verify(userNoteRepository).findById(1L);
        verify(userNoteRepository, times(0)).save(userNote);
    }

    @Test
    @DisplayName("Update user note content with invalid content - should throw TextLengthException")
    public void updateUserNoteContentWithInvalidContent()
    {
        UserNote userNote = new UserNote();
        userNote.setContent("content");
        userNote.setRemindAt(LocalDateTime.now().plusDays(1));
        UserNote returnedNote = new UserNote();
        String newContent = Collections.nCopies(257, "s").toString();
        returnedNote.setTitle(newContent);
        returnedNote.setRemindAt(LocalDateTime.now());

        when(userNoteRepository.findById(1L)).thenReturn(Optional.of(userNote));
        when(userNoteRepository.save(userNote)).thenReturn(returnedNote);
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Assertions.assertThrows(TextLengthException.class,
                () -> this.userNoteService.updateNoteTitle(1L, returnedNote.getTitle()));

        verify(userNoteRepository).findById(1L);
        verify(userNoteRepository, times(0)).save(userNote);
    }

    @Test
    @DisplayName("Update user note title")
    public void updateUserNoteTitle()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        UserNote returnedNote = new UserNote();
        returnedNote.setTitle("new title");

        when(userNoteRepository.findById(1L)).thenReturn(Optional.of(userNote));
        when(userNoteRepository.save(userNote)).thenReturn(returnedNote);
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        UserNote note = this.userNoteService.updateNoteTitle(1L, "new title");

        Assertions.assertEquals(userNote.getTitle(), returnedNote.getTitle());
        Assertions.assertEquals(note.getTitle(), returnedNote.getTitle());
        verify(userNoteRepository).save(userNote);
    }

    @Test
    @DisplayName("Update user note content")
    public void updateUserNoteContent()
    {
        UserNote userNote = new UserNote();
        userNote.setContent("content");
        UserNote returnedNote = new UserNote();
        returnedNote.setContent("new content");

        when(userNoteRepository.findById(1L)).thenReturn(Optional.of(userNote));
        when(userNoteRepository.save(userNote)).thenReturn(returnedNote);
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        UserNote note = this.userNoteService.updateNoteContent(1L, "new content");

        Assertions.assertEquals(userNote.getContent(), returnedNote.getContent());
        Assertions.assertEquals(note.getContent(), returnedNote.getContent());
        verify(userNoteRepository).save(userNote);
    }

    @Test
    @DisplayName("Update user note")
    public void updateUserNote()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        userNote.setContent("content");
        userNote.setId(1L);
        UserNote returnedNote = new UserNote();
        returnedNote.setId(1L);
        returnedNote.setTitle("title");
        returnedNote.setContent("new content");
        returnedNote.setRemindAt(LocalDateTime.now().plusDays(1));

        when(userNoteRepository.findById(1L)).thenReturn(Optional.of(userNote));
        when(userNoteRepository.save(userNote)).thenReturn(returnedNote);
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        UserNote note = this.userNoteService.updateUserNote(userNote);

        Assertions.assertEquals(note.getContent(), returnedNote.getContent());
        Assertions.assertEquals(note.getTitle(), returnedNote.getTitle());
        Assertions.assertEquals(note.getContent(), returnedNote.getContent());
        verify(userNoteRepository).save(userNote);
    }

    @Test
    @DisplayName("Get user notes")
    public void getUserNotes()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);

        when(userNoteRepository.findAllByUserId(1L, pageRequest))
                .thenReturn(new PageImpl<>(List.of(userNote), pageRequest, 1));
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Page<UserNote> notesResult = this.userNoteService.getAllUserNotes(0, 5, "title", true);

        Assertions.assertEquals(notesResult.getTotalElements(), 1);
        verify(userNoteRepository).findAllByUserId(1L, pageRequest);
    }

    @Test
    @DisplayName("Get user notes taken before now")
    public void getUserNotesTakenAtBeforeNow()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        LocalDateTime maxDate = LocalDateTime.now();

        when(userNoteRepository.findAllByUserIdAndTakenAtLessThanEqual(1L, maxDate, pageRequest))
                .thenReturn(new PageImpl<>(List.of(userNote), pageRequest, 1));
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Page<UserNote> notesResult = this.userNoteService
                .getAllUserNotesTakenBefore(maxDate, 0, 5, "title", true);

        Assertions.assertEquals(notesResult.getTotalElements(), 1);
        verify(userNoteRepository).findAllByUserIdAndTakenAtLessThanEqual(1L, maxDate, pageRequest);
    }

    @Test
    @DisplayName("Get user notes taken after a day ago")
    public void getUserNotesTakenAtAfterADayAgo()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        LocalDateTime minDate = LocalDateTime.now().minusDays(1);

        when(userNoteRepository.findAllByUserIdAndTakenAtGreaterThanEqual(1L, minDate, pageRequest))
                .thenReturn(new PageImpl<>(List.of(userNote), pageRequest, 1));
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Page<UserNote> notesResult = this.userNoteService
                .getAllUserNotesTakenAfter(minDate, 0, 5, "title", true);

        Assertions.assertEquals(notesResult.getTotalElements(), 1);
        verify(userNoteRepository).findAllByUserIdAndTakenAtGreaterThanEqual(1L, minDate, pageRequest);
    }

    @Test
    @DisplayName("Get user notes taken between a week ago and today")
    public void getUserNotesTakenAtBetweenAWeekAgoAndToday()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        LocalDateTime minDate = LocalDateTime.now().minusWeeks(1);
        LocalDateTime maxDate = LocalDateTime.now();

        when(userNoteRepository.findAllByUserIdAndTakenAtBetween(1L, minDate, maxDate, pageRequest))
                .thenReturn(new PageImpl<>(List.of(userNote), pageRequest, 1));
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Page<UserNote> notesResult = this.userNoteService
                .getAllUserNotesTakenBetween(minDate, maxDate, 0, 5, "title", true);

        Assertions.assertEquals(notesResult.getTotalElements(), 1);
        verify(userNoteRepository).findAllByUserIdAndTakenAtBetween(1L, minDate, maxDate, pageRequest);
    }

    @Test
    @DisplayName("Get user notes with reminder of before a week from now")
    public void getUserNotesRemindAtBeforeAWeekFromNow()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        LocalDateTime maxDate = LocalDateTime.now().plusWeeks(1);

        when(userNoteRepository.findAllByUserIdAndRemindAtLessThanEqual(1L, maxDate, pageRequest))
                .thenReturn(new PageImpl<>(List.of(userNote), pageRequest, 1));
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Page<UserNote> notesResult = this.userNoteService
                .getAllUserNotesRemindBefore(maxDate, 0, 5, "title", true);

        Assertions.assertEquals(notesResult.getTotalElements(), 1);
        verify(userNoteRepository).findAllByUserIdAndRemindAtLessThanEqual(1L, maxDate, pageRequest);
    }

    @Test
    @DisplayName("Get user notes with reminder of after a week from now")
    public void getUserNotesRemindAtAfterAWeekFromNow()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        LocalDateTime minDate = LocalDateTime.now().plusWeeks(1);

        when(userNoteRepository.findAllByUserIdAndRemindAtGreaterThanEqual(1L, minDate, pageRequest))
                .thenReturn(new PageImpl<>(List.of(userNote), pageRequest, 1));
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Page<UserNote> notesResult = this.userNoteService
                .getAllUserNotesRemindAfter(minDate, 0, 5, "title", true);

        Assertions.assertEquals(notesResult.getTotalElements(), 1);
        verify(userNoteRepository).findAllByUserIdAndRemindAtGreaterThanEqual(1L, minDate, pageRequest);
    }

    @Test
    @DisplayName("Get user notes with reminder of between a week ago and today")
    public void getUserNotesRemindAtBetweenAWeekAgoAndToday()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        LocalDateTime minDate = LocalDateTime.now().minusWeeks(1);
        LocalDateTime maxDate = LocalDateTime.now();

        when(userNoteRepository.findAllByUserIdAndRemindAtBetween(1L, minDate, maxDate, pageRequest))
                .thenReturn(new PageImpl<>(List.of(userNote), pageRequest, 1));
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        Page<UserNote> notesResult = this.userNoteService
                .getAllUserNotesRemindBetween(minDate, maxDate, 0, 5, "title", true);

        Assertions.assertEquals(notesResult.getTotalElements(), 1);
        verify(userNoteRepository).findAllByUserIdAndRemindAtBetween(1L, minDate, maxDate, pageRequest);
    }

    @Test
    @DisplayName("Get user notes results bt title")
    public void getUserNotesByTitleContainingIgnoreCase()
    {
        UserNote userNote = new UserNote();
        userNote.setTitle("title");
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);

        when(userNoteRepository.findAllByUserIdAndTitleContainingIgnoreCase(1L, "t", pageRequest))
                .thenReturn(List.of(new UserNoteSearchResult(){

                    @Override
                    public Long getId() {
                        return 1L;
                    }

                    @Override
                    public String getTitle() {
                        return "title";
                    }

                    @Override
                    public LocalDateTime getTakenAt() {
                        return LocalDateTime.now();
                    }

                    @Override
                    public LocalDateTime getRemindAt() {
                        return LocalDateTime.now().plusDays(1);
                    }
                }));
        mockedStatic.when(UserEvaluator::currentUserId).thenReturn(1L);

        List<UserNoteSearchResult> notesResult = this.userNoteService
                .getAllUserNotesByTitleExample("t" ,0, 5, "title", true);

        Assertions.assertFalse(notesResult.isEmpty());
        Assertions.assertEquals(notesResult.size(), 1);
        verify(userNoteRepository).findAllByUserIdAndTitleContainingIgnoreCase(1L, "t", pageRequest);
    }

    @Test
    @DisplayName("Delete user note")
    public void deleteNote()
    {
        UserNote userNote = new UserNote();
        userNote.setId(1L);

        when(userNoteRepository.findById(1L)).thenReturn(Optional.of(userNote));
        doNothing().when(userNoteRepository).deleteById(1L);

        this.userNoteService.deleteNote(1L);

        verify(userNoteRepository).deleteById(1L);
    }

}
