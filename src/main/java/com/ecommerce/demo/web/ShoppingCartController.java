package com.ecommerce.demo.web;

import com.ecommerce.demo.dto.ShoppingCartItemDto;
import com.ecommerce.demo.exception.GenericException;
import com.ecommerce.demo.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shoppingcart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public List<ShoppingCartItemDto> getShoppingCart() {
        return shoppingCartService.getShoppingCart();
    }

    @GetMapping("/{productId}")
    public ShoppingCartItemDto getShoppingCartId(@PathVariable int productId) throws GenericException {
        return shoppingCartService.getShoppingCartId(productId);
    }

    @PostMapping("/add")
    public void addProductToCart(
            @RequestParam int productId, @RequestParam(required = false, defaultValue = "1") int quantity) throws GenericException {
            shoppingCartService.addProductToCart(productId, quantity);
    }

    @PostMapping("/remove")
    public void removeProductFromCart(
            @RequestParam int productId, @RequestParam(required = false, defaultValue = "1") int quantity) throws GenericException {
            shoppingCartService.removeProductFromCart(productId, quantity);
    }
}
