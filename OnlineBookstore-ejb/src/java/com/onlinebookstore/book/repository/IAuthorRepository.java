package com.onlinebookstore.book.repository;

import com.onlinebookstore.book.entity.Authors;
import java.util.List;

public interface IAuthorRepository {
    List<Authors> findAll();
    List<Authors> searchByName(String name);
    Authors findById(int id);
    Authors findByName(String name);
    Authors save(Authors author);
    Authors update(Authors author);
    void delete(int id);
    long countBooksByAuthorId(int authorId);
}
