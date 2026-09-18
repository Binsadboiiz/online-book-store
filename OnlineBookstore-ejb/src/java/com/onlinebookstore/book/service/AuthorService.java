package com.onlinebookstore.book.service;

import com.onlinebookstore.book.dto.AuthorRequest;
import com.onlinebookstore.book.dto.AuthorResponse;
import com.onlinebookstore.book.entity.Authors;
import com.onlinebookstore.book.repository.IAuthorRepository;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.BadRequestException;
import com.onlinebookstore.common.exception.ConflictException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AuthorService {

    @Inject
    private IAuthorRepository authorRepository;

    public ApiResponse<List<AuthorResponse>> getAuthors(String search) {
        List<Authors> authors;
        if (search != null && !search.trim().isEmpty()) {
            authors = authorRepository.searchByName(search.trim());
        } else {
            authors = authorRepository.findAll();
        }

        List<AuthorResponse> responseList = authors.stream()
                .map(this::toAuthorResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("Authors fetched successfully", responseList);
    }

    public ApiResponse<AuthorResponse> getAuthorById(Integer id) {
        Authors author = authorRepository.findById(id);
        if (author == null) {
            throw new ResourceNotFoundException("Author not found with ID: " + id);
        }
        return ApiResponse.success("Author retrieved successfully", toAuthorResponse(author));
    }

    public ApiResponse<AuthorResponse> createAuthor(AuthorRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Author name cannot be empty");
        }

        Authors existing = authorRepository.findByName(request.getName().trim());
        if (existing != null) {
            throw new ConflictException("Author with name '" + request.getName().trim() + "' already exists");
        }

        Authors author = new Authors();
        author.setName(request.getName().trim());
        author.setBio(request.getBio() != null ? request.getBio().trim() : null);
        author.setCreatedAt(new Date());

        authorRepository.save(author);

        return ApiResponse.success("Author created successfully", toAuthorResponse(author));
    }

    public ApiResponse<AuthorResponse> updateAuthor(Integer id, AuthorRequest request) {
        Authors author = authorRepository.findById(id);
        if (author == null) {
            throw new ResourceNotFoundException("Author not found with ID: " + id);
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Author name cannot be empty");
        }

        Authors existingByName = authorRepository.findByName(request.getName().trim());
        if (existingByName != null && !existingByName.getId().equals(id)) {
            throw new ConflictException("Another author with name '" + request.getName().trim() + "' already exists");
        }

        author.setName(request.getName().trim());
        author.setBio(request.getBio() != null ? request.getBio().trim() : null);

        Authors updated = authorRepository.update(author);

        return ApiResponse.success("Author updated successfully", toAuthorResponse(updated));
    }

    public ApiResponse<String> deleteAuthor(Integer id) {
        Authors author = authorRepository.findById(id);
        if (author == null) {
            throw new ResourceNotFoundException("Author not found with ID: " + id);
        }

        long bookCount = authorRepository.countBooksByAuthorId(id);
        if (bookCount > 0) {
            throw new ConflictException("Cannot delete author with ID " + id + " because they have " + bookCount + " associated book(s).");
        }

        authorRepository.delete(id);
        return ApiResponse.success("Author deleted successfully", "Author ID " + id + " has been deleted.");
    }

    private AuthorResponse toAuthorResponse(Authors author) {
        long count = authorRepository.countBooksByAuthorId(author.getId());
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBio(),
                author.getCreatedAt(),
                count
        );
    }
}
