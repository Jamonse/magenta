package com.jsoft.magenta.notes;

import com.jsoft.magenta.users.ColorTheme;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NoteRepositoryTest
{
    @Autowired
    private UserNoteRepository userNoteRepository;

    @Autowired
    private UserRepository userRepository;

    private UserNote userNote;
    private User user;

    @BeforeEach
    private void init()
    {
        user = new User();
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setEmail("user@user.com");
        user.setPhoneNumber("050-5555555");
        user.setPassword("password");
        user.setBirthDay(LocalDate.now().minusYears(20));
        user.setEnabled(true);
        user.setPreferredTheme(ColorTheme.LIGHT);
        user.setCreatedAt(LocalDate.now().minusDays(20));
        userNote = new UserNote();
        userNote.setTitle("title");
        userNote.setContent("");
        userNote.setTakenAt(LocalDateTime.now());
        userNote.setRemindAt(LocalDateTime.now().plusDays(5));
        user.setNotes(Set.of(userNote));

        this.userRepository.save(user);
    }

    @Test
    @DisplayName("Get user notes")
    public void getUserNotes()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);

        Page<UserNote> notes = this.userNoteRepository.findAllByUserId(user.getId(), pageRequest);

        List<UserNote> notesList = notes.getContent();

        Assertions.assertFalse(notesList.isEmpty());
        Assertions.assertTrue(notesList.size() == 1);
        Assertions.assertEquals(notesList.get(0), userNote);
        UserNote note = notesList.get(0);
        Assertions.assertEquals(note.getTitle(), userNote.getTitle());
        Assertions.assertEquals(note.getUser(), userNote.getUser());
    }

    @Test
    @DisplayName("Get user notes before now or from now")
    public void getUserNotesByTakenAtBeforeNow()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);

        Page<UserNote> notes = this
                .userNoteRepository.findAllByUserIdAndTakenAtLessThanEqual(user.getId(), LocalDateTime.now(), pageRequest);

        List<UserNote> notesList = notes.getContent();

        Assertions.assertFalse(notesList.isEmpty());
        Assertions.assertTrue(notesList.size() == 1);
        Assertions.assertEquals(notesList.get(0), userNote);
        UserNote note = notesList.get(0);
        Assertions.assertEquals(note.getTitle(), userNote.getTitle());
        Assertions.assertEquals(note.getUser(), userNote.getUser());
    }

    @Test
    @DisplayName("Get user notes before yesterday or yesterday - should return empty page")
    public void getUserNotesByTakenAtBeforeYesterday()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);
        LocalDateTime takenAt = LocalDateTime.now().minusDays(1);
        Page<UserNote> notes = this
                .userNoteRepository.findAllByUserIdAndTakenAtLessThanEqual(user.getId(), takenAt, pageRequest);

        Assertions.assertEquals(notes.getTotalElements(), 0);
        Assertions.assertTrue(notes.getContent().isEmpty());
    }

    @Test
    @DisplayName("Get user notes after now or now - should return empty page")
    public void getUserNotesByTakenAtAfterNow()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);

        Page<UserNote> notes = this
                .userNoteRepository.findAllByUserIdAndTakenAtGreaterThanEqual(user.getId(), LocalDateTime.now(), pageRequest);

        Assertions.assertEquals(notes.getTotalElements(), 0);
        Assertions.assertTrue(notes.getContent().isEmpty());
    }

    @Test
    @DisplayName("Get user notes after yesterday")
    public void getUserNotesByTakenAtAfterYesterday()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);
        LocalDateTime takenAt = LocalDateTime.now().minusDays(1);
        Page<UserNote> notes = this
                .userNoteRepository.findAllByUserIdAndTakenAtGreaterThanEqual(user.getId(), takenAt, pageRequest);

        List<UserNote> notesList = notes.getContent();

        Assertions.assertFalse(notesList.isEmpty());
        Assertions.assertTrue(notesList.size() == 1);
        Assertions.assertEquals(notesList.get(0), userNote);
        UserNote note = notesList.get(0);
        Assertions.assertEquals(note.getTitle(), userNote.getTitle());
        Assertions.assertEquals(note.getUser(), userNote.getUser());
    }

    @Test
    @DisplayName("Get user notes between now")
    public void getUserNotesByTakenAtAfterAWeekAgoAndNow()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        Page<UserNote> notes = this
                .userNoteRepository.findAllByUserIdAndTakenAtBetween(user.getId(), startDate, LocalDateTime.now(), pageRequest);

        List<UserNote> notesList = notes.getContent();

        Assertions.assertFalse(notesList.isEmpty());
        Assertions.assertTrue(notesList.size() == 1);
        Assertions.assertEquals(notesList.get(0), userNote);
        UserNote note = notesList.get(0);
        Assertions.assertEquals(note.getTitle(), userNote.getTitle());
        Assertions.assertEquals(note.getUser(), userNote.getUser());
    }

    @Test
    @DisplayName("Get user notes with remind before two weeks from now")
    public void getUserNotesByRemindAtBeforeTwoWeeksFromNow()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);
        LocalDateTime endDate = LocalDateTime.now().plusWeeks(2);
        Page<UserNote> notes = this
                .userNoteRepository.findAllByUserIdAndRemindAtLessThanEqual(user.getId(), endDate, pageRequest);

        List<UserNote> notesList = notes.getContent();

        Assertions.assertFalse(notesList.isEmpty());
        Assertions.assertTrue(notesList.size() == 1);
        Assertions.assertEquals(notesList.get(0), userNote);
        UserNote note = notesList.get(0);
        Assertions.assertEquals(note.getTitle(), userNote.getTitle());
        Assertions.assertEquals(note.getUser(), userNote.getUser());
    }

    @Test
    @DisplayName("Get user notes with remind after a week from now - should return empty page")
    public void getUserNotesByRemindAtAfterAWeekFromNow()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);
        LocalDateTime startDate = LocalDateTime.now().plusWeeks(1);
        Page<UserNote> notes = this
                .userNoteRepository.findAllByUserIdAndRemindAtGreaterThanEqual(user.getId(), startDate, pageRequest);

        Assertions.assertEquals(notes.getTotalElements(), 0);
        Assertions.assertTrue(notes.getContent().isEmpty());
    }

    @Test
    @DisplayName("Get user notes with remind between tomorrow and two weeks from now")
    public void getUserNotesByRemindAtBetweenTomorrowAndTwoWeeksFromNow()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusWeeks(2);
        Page<UserNote> notes = this.userNoteRepository
                .findAllByUserIdAndRemindAtBetween(user.getId(), startDate, endDate, pageRequest);

        List<UserNote> notesList = notes.getContent();

        Assertions.assertFalse(notesList.isEmpty());
        Assertions.assertTrue(notesList.size() == 1);
        Assertions.assertEquals(notesList.get(0), userNote);
        UserNote note = notesList.get(0);
        Assertions.assertEquals(note.getTitle(), userNote.getTitle());
        Assertions.assertEquals(note.getUser(), userNote.getUser());
    }

    @Test
    @DisplayName("Get user notes by title example")
    public void getUserNotesResultsByTitleExample()
    {
        PageRequest pageRequest = PageRequest.of(0, 5);

        List<UserNoteSearchResult> notes = this
                .userNoteRepository.findAllByUserIdAndTitleContainingIgnoreCase(user.getId(), "t", pageRequest);

        Assertions.assertFalse(notes.isEmpty());
        Assertions.assertTrue(notes.size() == 1);
        UserNoteSearchResult note = notes.get(0);
        Assertions.assertEquals(note.getTitle(), userNote.getTitle());
    }
}
