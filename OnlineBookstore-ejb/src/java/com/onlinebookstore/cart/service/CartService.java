/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.cart.service;

import com.onlinebookstore.book.entity.Books;
import com.onlinebookstore.book.repository.IBookRepository;
import com.onlinebookstore.cart.dto.AddToCartRequest;
import com.onlinebookstore.cart.dto.CartResponse;
import com.onlinebookstore.cart.dto.UpdateCartItemRequest;
import com.onlinebookstore.cart.entity.Cart;
import com.onlinebookstore.cart.entity.CartItems;
import com.onlinebookstore.cart.repository.ICartRepository;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.BadRequestException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;
import com.onlinebookstore.user.entity.Users;
import com.onlinebookstore.user.repository.IUserRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.Date;

/**
 * Service implementation for Cart business logic.
 * 
 * @author ngnph
 */
@Stateless
public class CartService implements ICartService {

    @Inject
    private ICartRepository cartRepository;

    @Inject
    private IBookRepository bookRepository;

    @Inject
    private IUserRepository userRepository;

    @Override
    public ApiResponse<CartResponse> getCartByUserId(Integer userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Cart cart = getOrCreateCartForUser(user);
        return ApiResponse.success("Cart retrieved successfully", CartResponse.fromEntity(cart));
    }

    @Override
    public ApiResponse<CartResponse> addToCart(Integer userId, AddToCartRequest request) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        if (request == null || request.getBookId() == null || request.getQuantity() == null || request.getQuantity() < 1) {
            throw new BadRequestException("Invalid request data");
        }

        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Books book = bookRepository.findById(request.getBookId());
        if (book == null || !Boolean.TRUE.equals(book.getIsActive())) {
            throw new ResourceNotFoundException("Book not found or inactive");
        }

        Cart cart = getOrCreateCartForUser(user);
        CartItems existingItem = cartRepository.findCartItem(cart.getId(), book.getId());

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > book.getStockQuantity()) {
                throw new BadRequestException("Quantity exceeds available stock (" + book.getStockQuantity() + ")");
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setUpdatedAt(new Date());
            cartRepository.updateItem(existingItem);
        } else {
            if (request.getQuantity() > book.getStockQuantity()) {
                throw new BadRequestException("Quantity exceeds available stock (" + book.getStockQuantity() + ")");
            }
            CartItems newItem = new CartItems();
            newItem.setCartId(cart);
            newItem.setBookId(book);
            newItem.setQuantity(request.getQuantity());
            Date now = new Date();
            newItem.setCreatedAt(now);
            newItem.setUpdatedAt(now);
            cartRepository.saveItem(newItem);

            if (cart.getCartItemsCollection() != null) {
                cart.getCartItemsCollection().add(newItem);
            }
        }

        Cart updatedCart = cartRepository.findByUserId(userId);
        return ApiResponse.success("Item added to cart successfully", CartResponse.fromEntity(updatedCart));
    }

    @Override
    public ApiResponse<CartResponse> updateCartItem(Integer userId, Integer cartItemId, UpdateCartItemRequest request) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        if (request == null || request.getQuantity() == null || request.getQuantity() < 1) {
            throw new BadRequestException("Invalid product quantity");
        }

        CartItems item = cartRepository.findCartItemById(cartItemId);
        if (item == null || item.getCartId() == null || item.getCartId().getUserId() == null 
                || !item.getCartId().getUserId().getId().equals(userId)) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        Books book = item.getBookId();
        if (book != null && request.getQuantity() > book.getStockQuantity()) {
            throw new BadRequestException("Quantity exceeds available stock (" + book.getStockQuantity() + ")");
        }

        item.setQuantity(request.getQuantity());
        item.setUpdatedAt(new Date());
        cartRepository.updateItem(item);

        Cart updatedCart = cartRepository.findByUserId(userId);
        return ApiResponse.success("Cart item updated successfully", CartResponse.fromEntity(updatedCart));
    }

    @Override
    public ApiResponse<CartResponse> removeCartItem(Integer userId, Integer cartItemId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        CartItems item = cartRepository.findCartItemById(cartItemId);
        if (item == null || item.getCartId() == null || item.getCartId().getUserId() == null 
                || !item.getCartId().getUserId().getId().equals(userId)) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        cartRepository.deleteItem(cartItemId);

        Cart updatedCart = cartRepository.findByUserId(userId);
        return ApiResponse.success("Item removed from cart successfully", CartResponse.fromEntity(updatedCart));
    }

    @Override
    public ApiResponse<String> clearCart(Integer userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }

        cartRepository.clearCart(cart.getId());
        return ApiResponse.success("Cart cleared successfully", null);
    }

    private Cart getOrCreateCartForUser(Users user) {
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(user);
            Date now = new Date();
            cart.setCreatedAt(now);
            cart.setUpdatedAt(now);
            cart = cartRepository.save(cart);
        }
        return cart;
    }
}
