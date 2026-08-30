/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.onlinebookstore.repositories.interfaces;

import com.onlinebookstore.entities.Users;
import java.util.List;

/**
 *
 * @author ngnph
 */
public interface IUserRepository {
    Users findById(int id);
    
    Users findByUsername(String username);
    
    Users findByEmail(String email);
    
    Users findByUsernameOrEmail(String usernameOrEmail);
    
    List<Users> findAll();
    
    Users save(Users user);
    
    Users update(Users user);
    
    void delete(int id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
