package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.BookRequest;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(long id) {
        return getBook(id);
    }

    public Book create(BookRequest request) {
        return bookRepository.save(new Book(request.title(), request.author(), request.status()));
    }

    public Book update(long id, BookRequest request) {
        Book book = getBook(id);
        book.updateFrom(request);
        return bookRepository.save(book);
    }

    public void delete(long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }

    private Book getBook(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found: " + id));
    }
}
