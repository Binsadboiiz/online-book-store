package com.onlinebookstore.book.dto;

import java.util.Date;

public class AuthorResponse {
    private Integer id;
    private String name;
    private String bio;
    private Date createdAt;
    private long bookCount;

    public AuthorResponse() {
    }

    public AuthorResponse(Integer id, String name, String bio, Date createdAt, long bookCount) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.createdAt = createdAt;
        this.bookCount = bookCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getBookCount() {
        return bookCount;
    }

    public void setBookCount(long bookCount) {
        this.bookCount = bookCount;
    }
}
