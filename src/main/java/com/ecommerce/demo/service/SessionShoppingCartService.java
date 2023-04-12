package com.ecommerce.demo.service;

import com.ecommerce.demo.dto.ShoppingCartItemDto;
import com.ecommerce.demo.exception.GenericException;
import com.ecommerce.demo.session.SessionShoppingCart;

import javax.servlet.http.HttpServletRequest;

public interface SessionShoppingCartService {

    SessionShoppingCart getShoppingCart(HttpServletRequest request) throws GenericException;

    ShoppingCartItemDto getShoppingCartId(HttpServletRequest request, int productId) throws GenericException;

    void addProductToCart(HttpServletRequest request, int productId, int quantity) throws GenericException;

    void removeProductFromCart(HttpServletRequest request, int productId, int quantity) throws GenericException;
}
