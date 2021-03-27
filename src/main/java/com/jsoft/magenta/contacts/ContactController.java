package com.jsoft.magenta.contacts;

import static com.jsoft.magenta.util.AppDefaults.ASCENDING_SORT;
import static com.jsoft.magenta.util.AppDefaults.CONTACT_DEFAULT_SORT_NAME;
import static com.jsoft.magenta.util.AppDefaults.PAGE_INDEX;
import static com.jsoft.magenta.util.AppDefaults.PAGE_SIZE;
import static com.jsoft.magenta.util.AppDefaults.RESULTS_COUNT;

import com.jsoft.magenta.security.annotations.accounts.AccountManagePermission;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${application.url}contacts")
@RequiredArgsConstructor
public class ContactController {

  private final ContactService contactService;

  @PostMapping("{accountId}")
  @ResponseStatus(HttpStatus.CREATED)
  @AccountManagePermission
  public Contact createContact(
      @PathVariable Long accountId,
      @RequestBody @Valid Contact contact
  ) {
    return this.contactService.createContact(accountId, contact);
  }

  @PutMapping
  @AccountManagePermission
  public Contact updateContact(@RequestBody @Valid Contact contact) {
    return this.contactService.updateContact(contact);
  }

  @GetMapping("{accountId}")
  @AccountManagePermission
  public Page<Contact> getAllContacts(
      @PathVariable Long accountId,
      @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
      @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
      @RequestParam(required = false, defaultValue = CONTACT_DEFAULT_SORT_NAME) String sortBy,
      @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
  ) {
    return this.contactService.getAllContacts(accountId, pageIndex, pageSize, sortBy, asc);
  }

  @GetMapping("search/{accountId}")
  @AccountManagePermission
  public List<ContactSearchResult> getAllContactsResultByNameExample(
      @PathVariable Long accountId,
      @RequestParam String nameExample,
      @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount
  ) {
    return this.contactService.getAllContactsByNameExample(accountId, nameExample, resultsCount);
  }

  @DeleteMapping("{contactId}")
  @AccountManagePermission
  public void deleteContact(
      @PathVariable Long contactId
  ) {
    this.contactService.deleteContact(contactId);
  }

}
