package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.contacts.Contact;
import com.jsoft.magenta.contacts.ContactService;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.util.Stringify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private ContactService contactService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create account and get it back with generated id")
    public void createAccount() throws Exception {
        Account account = new Account();
        Account returnedAccount = new Account();
        returnedAccount.setId(1L);

        when(accountService.createAccount(account)).thenReturn(returnedAccount);

        mockMvc.perform(post(Stringify.BASE_URL + "accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Stringify.asJsonString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Stringify.asJsonString(returnedAccount)))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("Create association")
    public void createAssociation() throws Exception
    {
        doNothing().when(accountService).createAssociation(1L, 1L , AccessPermission.MANAGE);

        mockMvc.perform(post(Stringify.BASE_URL + "accounts/{accountId}/association/{userId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(AccessPermission.MANAGE.name()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(accountService).createAssociation(1L, 1L, AccessPermission.MANAGE);
    }

    @Test
    @DisplayName("Update association")
    public void updateAssociation() throws Exception
    {
        doNothing().when(accountService).updateAssociation(1L, 1L , AccessPermission.MANAGE);

        mockMvc.perform(patch(Stringify.BASE_URL + "accounts/{accountId}/association/{userId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(AccessPermission.MANAGE.name()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(accountService).updateAssociation(1L, 1L, AccessPermission.MANAGE);
    }

    @Test
    @DisplayName("Update account name")
    public void updateAccountName() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setName("name");

        Account returnedAccount = new Account();
        returnedAccount.setId(1L);
        returnedAccount.setName("new name");

        when(accountService.createAccount(account)).thenReturn(account);
        when(accountService.updateAccountName(account.getId(), "new name")).thenReturn(returnedAccount);

        mockMvc.perform(patch(Stringify.BASE_URL + "accounts/{accountId}", account.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("new name"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Stringify.asJsonString(returnedAccount)))
                .andExpect(jsonPath("$.name").value("new name"));
    }

    @Test
    @DisplayName("Get account by id")
    public void getAccountById() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setName("name");

        when(accountService.getAccountById(account.getId())).thenReturn(account);

        mockMvc.perform(get(Stringify.BASE_URL + "accounts/{accountId}", account.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Stringify.asJsonString(account)))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Get all accounts")
    public void getAllAccounts() throws Exception {
        Page<Account> accounts = new PageImpl<>(List.of(new Account()), PageRequest.of(0, 1), 1);

        when(accountService.getAllAccounts(0, 5, "name", false))
                .thenReturn(accounts);

        mockMvc.perform(get(Stringify.BASE_URL + "accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.content").exists());

        verify(accountService).getAllAccounts(0, 5, "name", false);
    }

    @Test
    @DisplayName("Delete account")
    public void deleteAccount() throws Exception {
        Account account = new Account();
        account.setId(1L);

        when(accountService.getAccountById(account.getId())).thenReturn(account);
        doNothing().when(accountService).deleteAccount(account.getId());

        mockMvc.perform(delete(Stringify.BASE_URL + "accounts/{accountId}", account.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Create contact")
    public void createContact() throws Exception {
        Account account = new Account();
        account.setId(1L);
        Contact contact = new Contact();
        contact.setId(1L);

        when(contactService.createContact(account.getId(), contact)).thenReturn(contact);

        mockMvc.perform(post(Stringify.BASE_URL + "accounts/contacts/{accountId}", account.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Stringify.asJsonString(contact)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Stringify.asJsonString(contact)))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("Update contact")
    public void updateContact() throws Exception {
        Account account = new Account();
        account.setId(1L);
        Contact contact = new Contact();
        contact.setId(1L);

        when(contactService.updateContact(contact)).thenReturn(contact);

        mockMvc.perform(put(Stringify.BASE_URL + "accounts/contacts/{accountId}", account.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Stringify.asJsonString(contact)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Stringify.asJsonString(contact)));
    }

    @Test
    @DisplayName("Get all contacts")
    public void getAllContacts() throws Exception {
        Page<Contact> contacts = new PageImpl<>(List.of(new Contact()), PageRequest.of(0, 5), 5);

        when(contactService.getAllContacts(1L, 0, 5, "firstName", false))
                .thenReturn(contacts);

        mockMvc.perform(get(Stringify.BASE_URL + "accounts/contacts/{accountId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.content").exists());

        verify(contactService).getAllContacts(1L, 0, 5, "firstName", false);
    }

    @Test
    @DisplayName("Delete contact")
    public void deleteContact() throws Exception
    {
        doNothing().when(contactService).deleteContact(1L, 1L);

        mockMvc.perform(delete(Stringify.BASE_URL + "accounts/contacts/{contactId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(contactService).deleteContact(1L, 1L);
    }

}