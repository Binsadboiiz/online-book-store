package com.onlinebookstore.book.service;

import com.onlinebookstore.book.dto.BookRequest;
import com.onlinebookstore.book.dto.BookResponse;
import com.onlinebookstore.book.dto.CategoryResponse;
import com.onlinebookstore.book.entity.Authors;
import com.onlinebookstore.book.entity.Books;
import com.onlinebookstore.book.entity.Categories;
import com.onlinebookstore.book.entity.Publishers;
import com.onlinebookstore.book.repository.IBookRepository;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.ConflictException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class BookService {

    @Inject
    private IBookRepository bookRepository;

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    public ApiResponse<List<BookResponse>> getBooks(String search, Integer categoryId, Integer authorId, Integer publisherId) {
        List<Books> books;
        if (search != null && !search.trim().isEmpty()) {
            books = bookRepository.search(search.trim());
        } else if (categoryId != null) {
            books = bookRepository.findByCategoryId(categoryId);
        } else if (authorId != null) {
            books = bookRepository.findByAuthorId(authorId);
        } else if (publisherId != null) {
            books = bookRepository.findByPublisherId(publisherId);
        } else {
            books = bookRepository.findAll();
        }

        List<BookResponse> responses = books.stream()
                .map(BookResponse::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.success("Fetched books successfully", responses);
    }

    public ApiResponse<List<BookResponse>> getTopSellingBooks(int limit) {
        List<Books> books = bookRepository.findTopSelling(limit > 0 ? limit : 10);
        List<BookResponse> responses = books.stream()
                .map(BookResponse::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success("Top selling books retrieved", responses);
    }

    public ApiResponse<List<CategoryResponse>> getCategories() {
        List<Categories> categories = entityManager.createQuery(
                "SELECT c FROM Categories c WHERE c.isActive = true ORDER BY c.name ASC", Categories.class)
                .getResultList();
        List<CategoryResponse> responses = categories.stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success("Categories retrieved successfully", responses);
    }

    public ApiResponse<BookResponse> getBookById(Integer id) {
        Books book = bookRepository.findById(id);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        return ApiResponse.success("Book details retrieved", BookResponse.fromEntity(book));
    }

    public ApiResponse<BookResponse> createBook(BookRequest request) {
        if (request.getIsbn() != null && !request.getIsbn().trim().isEmpty() 
                && bookRepository.existsByIsbn(request.getIsbn().trim())) {
            throw new ConflictException("ISBN already exists");
        }

        Books book = new Books();
        copyRequestToEntity(request, book);

        Date now = new Date();
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        bookRepository.save(book);
        return ApiResponse.success("Book created successfully", BookResponse.fromEntity(book));
    }

    public ApiResponse<BookResponse> updateBook(Integer id, BookRequest request) {
        Books book = bookRepository.findById(id);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }

        if (request.getIsbn() != null && !request.getIsbn().trim().isEmpty()
                && !request.getIsbn().trim().equals(book.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn().trim())) {
            throw new ConflictException("ISBN already exists");
        }

        copyRequestToEntity(request, book);
        book.setUpdatedAt(new Date());

        bookRepository.update(book);
        return ApiResponse.success("Book updated successfully", BookResponse.fromEntity(book));
    }

    public ApiResponse<String> deleteBook(Integer id) {
        boolean deleted = bookRepository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Book not found");
        }
        return ApiResponse.success("Book deleted successfully", null);
    }

    private void copyRequestToEntity(BookRequest request, Books book) {
        book.setTitle(request.getTitle().trim());
        book.setIsbn(request.getIsbn() != null ? request.getIsbn().trim() : null);
        book.setPrice(request.getPrice());
        book.setDiscountPrice(request.getDiscountPrice());
        book.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        book.setDescription(request.getDescription());
        book.setCoverImage(request.getCoverImage());
        book.setPublishedYear(request.getPublishedYear());
        book.setPages(request.getPages());
        book.setLanguage(request.getLanguage() != null ? request.getLanguage().trim() : "Tiếng Việt");
        book.setIsActive(request.getActive() != null ? request.getActive() : true);

        if (request.getAuthorId() != null) {
            book.setAuthorId(entityManager.find(Authors.class, request.getAuthorId()));
        } else if (request.getNewAuthorName() != null && !request.getNewAuthorName().trim().isEmpty()) {
            String authorName = request.getNewAuthorName().trim();
            Authors author = null;
            try {
                author = entityManager.createQuery("SELECT a FROM Authors a WHERE LOWER(a.name) = :name", Authors.class)
                        .setParameter("name", authorName.toLowerCase())
                        .setMaxResults(1)
                        .getSingleResult();
            } catch (jakarta.persistence.NoResultException e) {
                author = null;
            }

            if (author == null) {
                author = new Authors();
                author.setName(authorName);
                author.setBio(request.getNewAuthorBio() != null ? request.getNewAuthorBio().trim() : null);
                author.setCreatedAt(new Date());
                entityManager.persist(author);
            }
            book.setAuthorId(author);
        } else {
            book.setAuthorId(null);
        }

        if (request.getCategoryId() != null) {
            book.setCategoryId(entityManager.find(Categories.class, request.getCategoryId()));
        } else {
            book.setCategoryId(null);
        }

        if (request.getPublisherId() != null) {
            book.setPublisherId(entityManager.find(Publishers.class, request.getPublisherId()));
        } else {
            book.setPublisherId(null);
        }
    }
}
