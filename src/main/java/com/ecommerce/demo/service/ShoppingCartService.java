package com.ecommerce.demo.service;

import com.ecommerce.demo.dto.ProductDto;
import com.ecommerce.demo.dto.ShoppingCartItemDto;
import com.ecommerce.demo.exception.GenericException;

import java.util.List;

public interface ShoppingCartService {
    List<ShoppingCartItemDto> getShoppingCart();

    void addProductToCart(int productId, int quantity) throws GenericException;

    void removeProductFromCart(int productId, int quantity) throws GenericException;

    ShoppingCartItemDto getShoppingCartId(int id) throws GenericException;

}
