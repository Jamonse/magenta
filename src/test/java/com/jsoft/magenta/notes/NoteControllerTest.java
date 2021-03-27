package com.jsoft.magenta.notes;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jsoft.magenta.util.Stringify;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class NoteControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserNoteController userNoteController;

  @MockBean
  private UserNoteService userNoteService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create user note")
  public void createNote() throws Exception {
    UserNote userNote = new UserNote();
    userNote.setTitle("title");
    userNote.setContent("content");
    userNote.setTakenAt(LocalDateTime.now());
    UserNote returnedNote = new UserNote();
    returnedNote.setId(1L);
    returnedNote.setTitle("title");
    returnedNote.setContent("content");
    returnedNote.setTakenAt(LocalDateTime.now());

    when(userNoteService.createUserNote(userNote)).thenReturn(returnedNote);

    mockMvc.perform(post(Stringify.BASE_URL + "notes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(Stringify.asJsonString(userNote)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNotEmpty());

    verify(userNoteService).createUserNote(userNote);
  }

  @Test
  @DisplayName("Update note title")
  public void updateNoteTitle() throws Exception {
    UserNote userNote = new UserNote();
    userNote.setId(1L);
    userNote.setTitle("title");
    userNote.setContent("content");
    userNote.setTakenAt(LocalDateTime.now());
    UserNote returnedNote = new UserNote();
    returnedNote.setId(1L);
    returnedNote.setTitle("new title");
    returnedNote.setContent("content");
    returnedNote.setTakenAt(userNote.getTakenAt());

    when(userNoteService.updateNoteTitle(userNote.getId(), "new title")).thenReturn(returnedNote);

    mockMvc.perform(patch(Stringify.BASE_URL + "notes/title/{noteId}", userNote.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content("new title"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("new title"));

    verify(userNoteService).updateNoteTitle(userNote.getId(), "new title");
  }

  @Test
  @DisplayName("Update note content")
  public void updateNoteContent() throws Exception {
    UserNote userNote = new UserNote();
    userNote.setId(1L);
    userNote.setTitle("title");
    userNote.setContent("content");
    userNote.setTakenAt(LocalDateTime.now());
    UserNote returnedNote = new UserNote();
    returnedNote.setId(1L);
    returnedNote.setTitle("title");
    returnedNote.setContent("new content");
    returnedNote.setTakenAt(userNote.getTakenAt());

    when(userNoteService.updateNoteContent(userNote.getId(), "new content"))
        .thenReturn(returnedNote);

    mockMvc.perform(patch(Stringify.BASE_URL + "notes/content/{noteId}", userNote.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content("new content"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").value("new content"));

    verify(userNoteService).updateNoteContent(userNote.getId(), "new content");
  }

  @Test
  @DisplayName("Update note remind at")
  public void updateNoteRemindAt() throws Exception {
    UserNote userNote = new UserNote();
    userNote.setId(1L);
    userNote.setTitle("title");
    userNote.setContent("content");
    userNote.setTakenAt(LocalDateTime.now());
    UserNote returnedNote = new UserNote();
    returnedNote.setId(1L);
    returnedNote.setTitle("title");
    returnedNote.setContent("content");
    returnedNote.setTakenAt(userNote.getTakenAt());
    LocalDateTime remindAt = LocalDateTime.now().plusDays(1);
    returnedNote.setRemindAt(remindAt);

    when(userNoteService.updateUserNoteRemindTime(userNote.getId(), remindAt))
        .thenReturn(returnedNote);

    mockMvc.perform(patch(Stringify.BASE_URL + "notes/reminder/{noteId}", userNote.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("noteRemindTime", remindAt.toString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.remindAt").isNotEmpty());

    verify(userNoteService).updateUserNoteRemindTime(userNote.getId(), remindAt);
  }

  @Test
  @DisplayName("Update user note")
  public void updateNote() throws Exception {
    UserNote userNote = new UserNote();
    userNote.setId(1L);
    userNote.setTitle("title");
    userNote.setContent("content");
    userNote.setTakenAt(LocalDateTime.now());
    UserNote returnedNote = new UserNote();
    returnedNote.setId(1L);
    returnedNote.setTitle("new title");
    returnedNote.setContent("new content");
    returnedNote.setTakenAt(userNote.getTakenAt());
    LocalDateTime remindAt = LocalDateTime.now().plusDays(1);
    returnedNote.setRemindAt(remindAt);

    when(userNoteService.updateUserNote(userNote)).thenReturn(returnedNote);

    mockMvc.perform(put(Stringify.BASE_URL + "notes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(Stringify.asJsonString(userNote)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("new title"))
        .andExpect(jsonPath("$.content").value("new content"))
        .andExpect(jsonPath("$.remindAt").isNotEmpty());

    verify(userNoteService).updateUserNote(userNote);
  }

  @Test
  @DisplayName("Get all user notes")
  public void getAllNotes() throws Exception {
    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);

    when(userNoteService.getAllUserNotes(0, 5, "title", true))
        .thenReturn(new PageImpl<>(List.of(new UserNote()), pageRequest, 1));

    mockMvc.perform(get(Stringify.BASE_URL + "notes")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("pageIndex", "0")
        .queryParam("pageSize", "5")
        .queryParam("sortBy", "title")
        .queryParam("asc", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Get all user notes taken before")
  public void getAllNotesTakenBefore() throws Exception {
    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    LocalDateTime takenAt = LocalDateTime.now().minusDays(1);

    when(userNoteService.getAllUserNotesTakenBefore(takenAt, 0, 5, "title", true))
        .thenReturn(new PageImpl<>(List.of(new UserNote()), pageRequest, 1));

    mockMvc.perform(get(Stringify.BASE_URL + "notes/taken/before")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("maxDate", takenAt.toString())
        .queryParam("pageIndex", "0")
        .queryParam("pageSize", "5")
        .queryParam("sortBy", "title")
        .queryParam("asc", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Get all user notes taken after")
  public void getAllNotesTakenAfter() throws Exception {
    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    LocalDateTime takenAt = LocalDateTime.now().minusDays(1);

    when(userNoteService.getAllUserNotesTakenAfter(takenAt, 0, 5, "title", true))
        .thenReturn(new PageImpl<>(List.of(new UserNote()), pageRequest, 1));

    mockMvc.perform(get(Stringify.BASE_URL + "notes/taken/after")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("minDate", takenAt.toString())
        .queryParam("pageIndex", "0")
        .queryParam("pageSize", "5")
        .queryParam("sortBy", "title")
        .queryParam("asc", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Get all user notes taken between")
  public void getAllNotesTakenBetween() throws Exception {
    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    LocalDateTime endDate = LocalDateTime.now().minusDays(5);

    when(userNoteService.getAllUserNotesTakenBetween(startDate, endDate, 0, 5, "title", true))
        .thenReturn(new PageImpl<>(List.of(new UserNote()), pageRequest, 1));

    mockMvc.perform(get(Stringify.BASE_URL + "notes/taken/between")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("minDate", startDate.toString())
        .queryParam("maxDate", endDate.toString())
        .queryParam("pageIndex", "0")
        .queryParam("pageSize", "5")
        .queryParam("sortBy", "title")
        .queryParam("asc", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Get all user notes with reminder before")
  public void getAllNotesRemindAtBefore() throws Exception {
    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    LocalDateTime takenAt = LocalDateTime.now().minusDays(1);

    when(userNoteService.getAllUserNotesRemindBefore(takenAt, 0, 5, "title", true))
        .thenReturn(new PageImpl<>(List.of(new UserNote()), pageRequest, 1));

    mockMvc.perform(get(Stringify.BASE_URL + "notes/reminder/before")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("maxDate", takenAt.toString())
        .queryParam("pageIndex", "0")
        .queryParam("pageSize", "5")
        .queryParam("sortBy", "title")
        .queryParam("asc", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Get all user notes with reminder after")
  public void getAllNotesRemindAtAfter() throws Exception {
    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    LocalDateTime takenAt = LocalDateTime.now().minusDays(1);

    when(userNoteService.getAllUserNotesRemindAfter(takenAt, 0, 5, "title", true))
        .thenReturn(new PageImpl<>(List.of(new UserNote()), pageRequest, 1));

    mockMvc.perform(get(Stringify.BASE_URL + "notes/reminder/after")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("minDate", takenAt.toString())
        .queryParam("pageIndex", "0")
        .queryParam("pageSize", "5")
        .queryParam("sortBy", "title")
        .queryParam("asc", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Get all user notes with reminder between")
  public void getAllNotesRemindAtBetween() throws Exception {
    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    LocalDateTime endDate = LocalDateTime.now().minusDays(5);

    when(userNoteService.getAllUserNotesRemindBetween(startDate, endDate, 0, 5, "title", true))
        .thenReturn(new PageImpl<>(List.of(new UserNote()), pageRequest, 1));

    mockMvc.perform(get(Stringify.BASE_URL + "notes/reminder/between")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("minDate", startDate.toString())
        .queryParam("maxDate", endDate.toString())
        .queryParam("pageIndex", "0")
        .queryParam("pageSize", "5")
        .queryParam("sortBy", "title")
        .queryParam("asc", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Get all notes by title example")
  public void getAllUserNotesByTitleExample() throws Exception {
    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    UserNoteSearchResult userNoteSearchResult = new UserNoteSearchResult() {
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
        return LocalDateTime.now().minusDays(1);
      }

      @Override
      public LocalDateTime getRemindAt() {
        return LocalDateTime.now().plusDays(1);
      }
    };

    when(userNoteService.getAllUserNotesByTitleExample("t", 0, 5, "title", true))
        .thenReturn(List.of(userNoteSearchResult));

    mockMvc.perform(get(Stringify.BASE_URL + "notes/by/title")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("titleExample", "t")
        .queryParam("pageIndex", "0")
        .queryParam("pageSize", "5")
        .queryParam("sortBy", "title")
        .queryParam("asc", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].title").value("title"));
  }

  @Test
  @DisplayName("Delete note")
  public void deleteNote() throws Exception {
    doNothing().when(userNoteService).deleteNote(1L);

    mockMvc.perform(delete(Stringify.BASE_URL + "notes/{noteId}", 1L)
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

}
