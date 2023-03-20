package com.ecommerce.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShoppingCartItemDto {

    private ProductDto productDto;
    private int quantity;
    private BigDecimal totalPrice;

}
