/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.onlinebookstore.cart.service;

import com.onlinebookstore.cart.dto.AddToCartRequest;
import com.onlinebookstore.cart.dto.CartResponse;
import com.onlinebookstore.cart.dto.UpdateCartItemRequest;
import com.onlinebookstore.common.dto.ApiResponse;

/**
 * Service interface for shopping cart business logic operations.
 * 
 * @author ngnph
 */
public interface ICartService {
    ApiResponse<CartResponse> getCartByUserId(Integer userId);
    
    ApiResponse<CartResponse> addToCart(Integer userId, AddToCartRequest request);
    
    ApiResponse<CartResponse> updateCartItem(Integer userId, Integer cartItemId, UpdateCartItemRequest request);
    
    ApiResponse<CartResponse> removeCartItem(Integer userId, Integer cartItemId);
    
    ApiResponse<String> clearCart(Integer userId);
}
