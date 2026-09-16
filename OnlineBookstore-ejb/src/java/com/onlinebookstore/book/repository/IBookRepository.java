/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.book.repository;

import com.onlinebookstore.book.entity.Books;
import java.util.List;

/**
 *
 * @author ngnph
 */
public interface IBookRepository {
    Books findById(Integer id);
    Books findByIsbn(String isbn);
    List<Books> findAll();
    List<Books> search(String keyword);
    Books save(Books book);
    Books update(Books book);
    boolean deleteById(Integer id);
    
    List<Books> findByCategoryId(Integer categoryId);
    List<Books> findByAuthorId(Integer authorId);
    List<Books> findByPublisherId(Integer publisherId);
    List<Books> findByActiveBooks();
    boolean existsByIsbn(String isbn);

    int deductStock(Integer bookId, int quantity);
    int restock(Integer bookId, int quantity);
}
