package com.ecommerce.demo.service.impl;

import com.ecommerce.demo.cache.IInventoryCache;
import com.ecommerce.demo.cache.IProductCache;
import com.ecommerce.demo.dto.ProductDto;
import com.ecommerce.demo.dto.ShoppingCartItemDto;
import com.ecommerce.demo.exception.GenericException;
import com.ecommerce.demo.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final List<ShoppingCartItemDto> shoppingCart = new ArrayList<>();
    private final IProductCache productCache;
    private final IInventoryCache iInventoryCache;
    private LocalDateTime lastUpdate = null;


    @Override
    public List<ShoppingCartItemDto> getShoppingCart() {
        return shoppingCart;
    }

    /*Agrega el producto al carrito con el productId y quantity*/
    @Override
    public void addProductToCart(int productId, int quantity) throws GenericException {
        log.info("addProductToCart productId {} quantity {}", productId, quantity);
        ProductDto productToAdd = getProductById(productId);
        if(productToAdd == null) {
            throw new GenericException("Not found productId " + productId);
        }
        if (reduceStock(productId, quantity)) {
            Optional<ShoppingCartItemDto> existingItem = shoppingCart.stream()
                    .filter(item -> item.getProductDto().getIdentification() == productId)
                    .findFirst();
            if (existingItem.isPresent()) {
                ShoppingCartItemDto cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                BigDecimal totalPrice = calculateTotalPrice(productToAdd.getImporte(), cartItem.getQuantity());
                cartItem.setTotalPrice(totalPrice);
            } else {
                BigDecimal totalPrice = calculateTotalPrice(productToAdd.getImporte(), quantity);
                shoppingCart.add(new ShoppingCartItemDto(productToAdd, quantity, totalPrice));
            }
            lastUpdate = LocalDateTime.now();
        } else {
            throw new GenericException("there is no stock "+ quantity + " available for the value sent");
        }
    }

    /*Elimina el producto al carrito con el productId y quantity*/
    @Override
    public void removeProductFromCart(int productId, int quantity) throws GenericException {
        log.info("removeProductFromCart productId {} quantity {}", productId, quantity);
        Optional<ShoppingCartItemDto> existingItem = shoppingCart.stream()
                .filter(item -> item.getProductDto().getIdentification() == productId)
                .findFirst();
        log.info("removeProductFromCart existingItem {}", existingItem);
        if(existingItem.isEmpty()) {
           throw new GenericException("product in cart is empty");
        }
        if (existingItem.get().getQuantity() >= quantity) {
            ShoppingCartItemDto cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() - quantity);
            BigDecimal totalPrice = calculateTotalPrice(cartItem.getProductDto().getImporte(), cartItem.getQuantity());
            cartItem.setTotalPrice(totalPrice);
            increaseStock(productId, quantity);
            if (existingItem.get().getQuantity() == 0) {
                shoppingCart.remove(existingItem.get());
            }
            lastUpdate = LocalDateTime.now();
        } else {
            throw new GenericException("quantity of stock "+quantity +" to reduce shipped not available");
        }
    }

    @Override
    public ShoppingCartItemDto getShoppingCartId(int id) throws GenericException {
        return shoppingCart.stream()
                .filter(x -> x.getProductDto().getIdentification().equals(id)).findFirst().orElseThrow( () -> new GenericException("added product does not exist"));
    }

    private ProductDto getProductById(int id) {
        for (ProductDto product : productCache.getProducts()) {
            if (product.getIdentification() == id) {
                return product;
            }
        }
        return null;
    }

    private boolean increaseStock(int id, int quantity) {
        return iInventoryCache.increaseStock(id, quantity);
    }

    private boolean reduceStock(int id, int quantity) {
        return iInventoryCache.reduceStock(id, quantity);
    }

    private BigDecimal calculateTotalPrice(BigDecimal unitPrice, int quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /* Configuración del cron para que ejecute cada minuto y según la fecha de actualización pueda evaluar
    si ya pasaron 10 minutos expresado en segundos 600*/
    @Scheduled(cron = "0 * * * * ?")
    public void clearShoppingCart() {
        log.info("cron consulting...");
        LocalDateTime dateNow = LocalDateTime.now();
        if (lastUpdate != null && dateNow.isAfter(lastUpdate.plusSeconds(600))) {
            log.info("lastUpdate lastUpdate{} dateNow {}", lastUpdate, dateNow);
            log.info("Clearing shopping cart cache...");
            List<ShoppingCartItemDto> shoppingCartAux = new ArrayList<>(shoppingCart);
            shoppingCartAux.stream().filter(Objects::nonNull).forEach(s-> {
                try {
                    removeProductFromCart(s.getProductDto().getIdentification(), s.getQuantity());
                } catch (GenericException e) {
                    throw new RuntimeException(e);
                }
            });
            lastUpdate = null;
        }
    }
}
