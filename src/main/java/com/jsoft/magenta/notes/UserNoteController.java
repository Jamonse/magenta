package com.jsoft.magenta.notes;

import com.jsoft.magenta.util.validation.ValidContent;
import com.jsoft.magenta.util.validation.ValidTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static com.jsoft.magenta.util.AppDefaults.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${application.url}notes")
public class UserNoteController
{
    private final UserNoteService userNoteService;
    private final String DEFAULT_NOTE_SORT = "title";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserNote createNote(@RequestBody @Valid UserNote userNote)
    {
        return this.userNoteService.createUserNote(userNote);
    }

    @PatchMapping("title/{noteId}")
    public UserNote updateNoteTitle(
            @PathVariable Long noteId,
            @RequestBody @ValidTitle String noteTitle
    )
    {
        return this.userNoteService.updateNoteTitle(noteId, noteTitle);
    }

    @PatchMapping("content/{noteId}")
    public UserNote updateNoteContent(
            @PathVariable Long noteId,
            @RequestBody @ValidContent String noteContent
    )
    {
        return this.userNoteService.updateNoteContent(noteId, noteContent);
    }

    @PatchMapping("reminder/{noteId}")
    public UserNote updateNoteRemindTime(
            @PathVariable Long noteId,
            @RequestParam("noteRemindTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime noteRemindTime
    )
    {
        return this.userNoteService.updateUserNoteRemindTime(noteId, noteRemindTime);
    }

    @PutMapping
    public UserNote updateUserNote(@RequestBody @Valid UserNote userNote)
    {
        return this.userNoteService.updateUserNote(userNote);
    }

    @GetMapping
    public Page<UserNote> getAllUserNotes(
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_NOTE_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.userNoteService.getAllUserNotes(pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("taken/before")
    public Page<UserNote> getAllUserNotesTakenBefore(
            @RequestParam("maxDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxDate,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_NOTE_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.userNoteService.getAllUserNotesTakenBefore(maxDate, pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("taken/after")
    public Page<UserNote> getAllUserNotesTakenAfter(
            @RequestParam("minDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minDate,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_NOTE_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.userNoteService.getAllUserNotesTakenAfter(minDate, pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("taken/between")
    public Page<UserNote> getAllUserNotesTakenBetween(
            @RequestParam("minDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minDate,
            @RequestParam("maxDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxDate,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_NOTE_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.userNoteService.getAllUserNotesTakenBetween(minDate, maxDate, pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("reminder/before")
    public Page<UserNote> getAllUserNotesRemindBefore(
            @RequestParam("maxDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxDate,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_NOTE_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.userNoteService.getAllUserNotesRemindBefore(maxDate, pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("reminder/after")
    public Page<UserNote> getAllUserNotesRemindAfter(
            @RequestParam("minDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minDate,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_NOTE_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.userNoteService.getAllUserNotesRemindAfter(minDate, pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("reminder/between")
    public Page<UserNote> getAllUserNotesRemindBetween(
            @RequestParam("minDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minDate,
            @RequestParam("maxDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxDate,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_NOTE_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.userNoteService.getAllUserNotesRemindBetween(minDate, maxDate, pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("by/title")
    public List<UserNoteSearchResult> getAllUserNotesByTitleExample(
            @RequestParam("titleExample") String titleExample,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_NOTE_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.userNoteService.getAllUserNotesByTitleExample(titleExample, pageIndex, pageSize, sortBy, asc);
    }

    @DeleteMapping("{noteId}")
    public void deleteUserNote(@PathVariable Long noteId)
    {
        this.userNoteService.deleteNote(noteId);
    }

}
