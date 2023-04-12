package com.ecommerce.demo.web;

import com.ecommerce.demo.dto.ShoppingCartItemDto;
import com.ecommerce.demo.exception.GenericException;
import com.ecommerce.demo.service.SessionShoppingCartService;
import com.ecommerce.demo.session.SessionShoppingCart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/shoppingcart")
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartController {
    private final SessionShoppingCartService shoppingCartService;

    @GetMapping
    public SessionShoppingCart getShoppingCart(HttpServletRequest request) throws GenericException {
        return shoppingCartService.getShoppingCart(request);
    }

    @GetMapping("/{productId}")
    public ShoppingCartItemDto getShoppingCartId(@PathVariable int productId, HttpServletRequest request) throws GenericException {
        return shoppingCartService.getShoppingCartId(request, productId);
    }

    @PostMapping("/add")
    public void addProductToCart(
            @RequestParam int productId, @RequestParam(required = false, defaultValue = "1") int quantity, HttpServletRequest request) throws GenericException {
        shoppingCartService.addProductToCart(request, productId, quantity);
    }

    @PostMapping("/remove")
    public void removeProductFromCart(
            @RequestParam int productId, @RequestParam(required = false, defaultValue = "1") int quantity, HttpServletRequest request) throws GenericException {
        shoppingCartService.removeProductFromCart(request, productId, quantity);
    }
}
