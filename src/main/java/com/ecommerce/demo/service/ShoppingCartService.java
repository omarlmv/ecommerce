package com.ecommerce.demo.service;

import com.ecommerce.demo.dto.ShoppingCartItemDto;
import com.ecommerce.demo.exception.GenericException;
import com.ecommerce.demo.session.SessionShoppingCart;

import java.util.UUID;

public interface ShoppingCartService {
    SessionShoppingCart getShoppingCart(UUID sessionId);

    void addProductToCart(UUID sessionId, int productId, int quantity) throws GenericException;

    void removeProductFromCart(UUID sessionId, int productId, int quantity) throws GenericException;

    ShoppingCartItemDto getShoppingCartId(UUID sessionId, int id) throws GenericException;

    SessionShoppingCart getSessionCart(UUID sessionId);
}
