/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.cart.repository;

import com.onlinebookstore.cart.entity.Cart;
import com.onlinebookstore.cart.entity.CartItems;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

/**
 * Implementation of ICartRepository interface using JPA EntityManager.
 * 
 * @author ngnph
 */
@Stateless
public class CartRepositoryImpl implements ICartRepository {

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    @Override
    public Cart findByUserId(Integer userId) {
        try {
            Cart cart = entityManager.createQuery(
                    "SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.cartItemsCollection WHERE c.userId.id = :userId", 
                    Cart.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            if (cart != null) {
                entityManager.refresh(cart);
            }
            return cart;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Cart findById(Integer id) {
        Cart cart = entityManager.find(Cart.class, id);
        if (cart != null) {
            entityManager.refresh(cart);
        }
        return cart;
    }

    @Override
    public Cart save(Cart cart) {
        entityManager.persist(cart);
        entityManager.flush();
        return cart;
    }

    @Override
    public Cart update(Cart cart) {
        Cart merged = entityManager.merge(cart);
        entityManager.flush();
        return merged;
    }

    @Override
    public CartItems findCartItem(Integer cartId, Integer bookId) {
        try {
            return entityManager.createQuery(
                    "SELECT ci FROM CartItems ci WHERE ci.cartId.id = :cartId AND ci.bookId.id = :bookId", 
                    CartItems.class)
                    .setParameter("cartId", cartId)
                    .setParameter("bookId", bookId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public CartItems findCartItemById(Integer cartItemId) {
        return entityManager.find(CartItems.class, cartItemId);
    }

    @Override
    public CartItems saveItem(CartItems item) {
        entityManager.persist(item);
        entityManager.flush();
        if (item.getCartId() != null) {
            entityManager.refresh(item.getCartId());
        }
        return item;
    }

    @Override
    public CartItems updateItem(CartItems item) {
        CartItems merged = entityManager.merge(item);
        entityManager.flush();
        if (merged.getCartId() != null) {
            entityManager.refresh(merged.getCartId());
        }
        return merged;
    }

    @Override
    public boolean deleteItem(Integer cartItemId) {
        CartItems item = findCartItemById(cartItemId);
        if (item != null) {
            Cart cart = item.getCartId();
            entityManager.remove(item);
            entityManager.flush();
            if (cart != null) {
                entityManager.refresh(cart);
            }
            return true;
        }
        return false;
    }

    @Override
    public void clearCart(Integer cartId) {
        Cart cart = findById(cartId);
        entityManager.createQuery("DELETE FROM CartItems ci WHERE ci.cartId.id = :cartId")
                .setParameter("cartId", cartId)
                .executeUpdate();
        entityManager.flush();
        if (cart != null) {
            entityManager.refresh(cart);
        }
    }
}
