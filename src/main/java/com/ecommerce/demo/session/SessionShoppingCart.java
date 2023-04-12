package com.ecommerce.demo.session;

import com.ecommerce.demo.dto.ShoppingCartItemDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SessionShoppingCart {
    private final List<ShoppingCartItemDto> shoppingCartItems = new ArrayList<>();

    private LocalDateTime lastUpdate = null;

}
