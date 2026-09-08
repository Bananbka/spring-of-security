package com.example.demo.book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book findById(@PathVariable long id) {
        return getBook(id);
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody BookRequest request) {
        Book book = bookRepository.save(new Book(request.title(), request.author(), request.status()));
        return ResponseEntity.created(URI.create("/api/books/" + book.getId())).body(book);
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable long id, @RequestBody BookRequest request) {
        Book book = getBook(id);
        book.updateFrom(request);
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Book not found: " + id);
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Book getBook(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found: " + id));
    }
}
