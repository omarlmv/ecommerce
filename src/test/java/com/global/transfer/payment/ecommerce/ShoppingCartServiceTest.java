package com.global.transfer.payment.ecommerce;

import com.ecommerce.demo.cache.IInventoryCache;
import com.ecommerce.demo.cache.ProductCache;
import com.ecommerce.demo.dto.ProductDto;
import com.ecommerce.demo.dto.ShoppingCartItemDto;
import com.ecommerce.demo.repository.ProductRepository;
import com.ecommerce.demo.service.impl.ShoppingCartServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartServiceTest {

    @Mock
    private ProductCache productCache;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IInventoryCache iInventoryCache;


    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    public void addProductToShoppingCart() throws Exception {
        int productId = 1;
        int quantity = 2;

        ProductDto productDto = new ProductDto(productId, "Product test", BigDecimal.ONE, 10);

        //Configuración de los métodos simulados
        //when(productRepository.getProductsJson()).thenReturn(Arrays.asList(productDto));
        when(productCache.getProducts()).thenReturn(Arrays.asList(productDto));
        when(productCache.reduceStock(productId, quantity)).thenReturn(true);
        //when(iInventoryCache.reduceStock(productId, quantity)).thenReturn(true);

        //invocación para agregar al carrito
        shoppingCartService.addProductToCart(productId, quantity);
        List<ShoppingCartItemDto> shoppingCartItemDtos = shoppingCartService.getShoppingCart();

        Assertions.assertEquals(shoppingCartItemDtos.get(0).getProductDto(), productDto);
    }

}
