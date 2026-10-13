package com.example.demo;

import com.example.demo.config.SecurityConfig;
import com.example.demo.controller.AdminController;
import com.example.demo.controller.BookController;
import com.example.demo.controller.SuperAdminController;
import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Access tests per Lecture 8: role-based access control for the
 * admin / user / superadmin accounts declared in {@link SecurityConfig}.
 */
@WebMvcTest(controllers = {BookController.class, AdminController.class, SuperAdminController.class})
@Import(SecurityConfig.class)
class AccessControlTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private static final String BOOK_JSON = """
            {"title": "The Left Hand of Darkness", "author": "Ursula K. Le Guin", "status": "TO_READ"}
            """;

    @Test
    void user_canListBooks() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(new Book("T", "A", "TO_READ")));

        mockMvc.perform(get("/api/books").with(httpBasic("user", "user")))
                .andExpect(status().isOk());
    }

    @Test
    void superadmin_cannotListBooks() throws Exception {
        mockMvc.perform(get("/api/books").with(httpBasic("superadmin", "superadmin")))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canReadOneBook() throws Exception {
        when(bookService.findById(1L)).thenReturn(new Book("T", "A", "TO_READ"));

        mockMvc.perform(get("/api/books/1").with(httpBasic("admin", "admin")))
                .andExpect(status().isOk());
    }

    @Test
    void admin_canCreateBook() throws Exception {
        when(bookService.create(any())).thenReturn(new Book("T", "A", "TO_READ"));

        mockMvc.perform(post("/api/books")
                        .with(httpBasic("admin", "admin"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void user_cannotCreateBook() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(httpBasic("user", "user"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canUpdateBook() throws Exception {
        when(bookService.update(anyLong(), any())).thenReturn(new Book("T", "A", "READING"));

        mockMvc.perform(put("/api/books/1")
                        .with(httpBasic("admin", "admin"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void user_cannotUpdateBook() throws Exception {
        mockMvc.perform(put("/api/books/1")
                        .with(httpBasic("user", "user"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1")
                        .with(httpBasic("admin", "admin"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void user_cannotAccessAdminRoute() throws Exception {
        mockMvc.perform(get("/api/admin/hello").with(httpBasic("user", "user")))
                .andExpect(status().isForbidden());
    }

    @Test
    void superadmin_canAccessSuperadminRoute() throws Exception {
        mockMvc.perform(get("/api/superadmin/hello").with(httpBasic("superadmin", "superadmin")))
                .andExpect(status().isOk());
    }
}
