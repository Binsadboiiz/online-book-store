package com.onlinebookstore.book.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthorRequest {

    @NotNull(message = "Author name is required")
    @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
    private String name;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;

    public AuthorRequest() {
    }

    public AuthorRequest(String name, String bio) {
        this.name = name;
        this.bio = bio;
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
}
