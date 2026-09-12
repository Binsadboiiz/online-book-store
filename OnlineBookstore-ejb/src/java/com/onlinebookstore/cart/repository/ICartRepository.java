/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.onlinebookstore.cart.repository;

import com.onlinebookstore.cart.entity.Cart;
import com.onlinebookstore.cart.entity.CartItems;

/**
 * Repository interface for Cart and CartItems persistence operations.
 * 
 * @author ngnph
 */
public interface ICartRepository {
    Cart findByUserId(Integer userId);
    
    Cart findById(Integer id);
    
    Cart save(Cart cart);
    
    Cart update(Cart cart);
    
    CartItems findCartItem(Integer cartId, Integer bookId);
    
    CartItems findCartItemById(Integer cartItemId);
    
    CartItems saveItem(CartItems item);
    
    CartItems updateItem(CartItems item);
    
    boolean deleteItem(Integer cartItemId);
    
    void clearCart(Integer cartId);
}
