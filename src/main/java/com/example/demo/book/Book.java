package com.example.demo.book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String status;

    protected Book() {
    }

    public Book(String title, String author, String status) {
        this.title = title;
        this.author = author;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getStatus() {
        return status;
    }

    public void updateFrom(BookRequest request) {
        this.title = request.title();
        this.author = request.author();
        this.status = request.status();
    }
}
