package com.devinlab.controller;

import com.devinlab.model.Book;
import com.devinlab.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllBooks_shouldReturnListOfBooks() throws Exception {
        Book book1 = new Book(1L, "Spring in Action", "Craig Walls", "978-1617294945", 49.99);
        Book book2 = new Book(2L, "Clean Code", "Robert Martin", "978-0132350884", 39.99);
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(book1, book2));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Spring in Action")))
                .andExpect(jsonPath("$[0].author", is("Craig Walls")))
                .andExpect(jsonPath("$[1].title", is("Clean Code")))
                .andExpect(jsonPath("$[1].author", is("Robert Martin")));
    }

    @Test
    void getAllBooks_shouldReturnEmptyListWhenNoBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getBookById_shouldReturnBookWhenFound() throws Exception {
        Book book = new Book(1L, "Spring in Action", "Craig Walls", "978-1617294945", 49.99);
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Spring in Action")))
                .andExpect(jsonPath("$.author", is("Craig Walls")))
                .andExpect(jsonPath("$.isbn", is("978-1617294945")))
                .andExpect(jsonPath("$.price", is(49.99)));
    }

    @Test
    void getBookById_shouldReturn404WhenNotFound() throws Exception {
        when(bookService.getBookById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBook_shouldReturnCreatedBook() throws Exception {
        Book bookToCreate = new Book(null, "New Book", "New Author", "978-0000000000", 29.99);
        Book createdBook = new Book(1L, "New Book", "New Author", "978-0000000000", 29.99);
        when(bookService.createBook(any(Book.class))).thenReturn(createdBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("New Book")))
                .andExpect(jsonPath("$.author", is("New Author")))
                .andExpect(jsonPath("$.isbn", is("978-0000000000")))
                .andExpect(jsonPath("$.price", is(29.99)));
    }

    @Test
    void updateBook_shouldReturnUpdatedBook() throws Exception {
        Book bookDetails = new Book(null, "Updated Title", "Updated Author", "978-1111111111", 59.99);
        Book updatedBook = new Book(1L, "Updated Title", "Updated Author", "978-1111111111", 59.99);
        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.author", is("Updated Author")))
                .andExpect(jsonPath("$.isbn", is("978-1111111111")))
                .andExpect(jsonPath("$.price", is(59.99)));
    }

    @Test
    void updateBook_shouldReturn500WhenNotFound() throws Exception {
        Book bookDetails = new Book(null, "Updated Title", "Updated Author", "978-1111111111", 59.99);
        when(bookService.updateBook(eq(99L), any(Book.class)))
                .thenThrow(new RuntimeException("Book not found with id: 99"));

        mockMvc.perform(put("/api/books/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDetails)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteBook_shouldReturn204WhenDeleted() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(1L);
    }

    @Test
    void deleteBook_shouldReturn500WhenNotFound() throws Exception {
        doThrow(new RuntimeException("Book not found with id: 99"))
                .when(bookService).deleteBook(99L);

        mockMvc.perform(delete("/api/books/99"))
                .andExpect(status().isInternalServerError());
    }
}
