package com.ecommerce.demo.service.impl;

import com.ecommerce.demo.cache.IInventoryCache;
import com.ecommerce.demo.cache.IProductCache;
import com.ecommerce.demo.dto.ProductDto;
import com.ecommerce.demo.dto.ShoppingCartItemDto;
import com.ecommerce.demo.exception.GenericException;
import com.ecommerce.demo.service.ShoppingCartService;
import com.ecommerce.demo.session.SessionShoppingCart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final IProductCache productCache;
    private final IInventoryCache iInventoryCache;

    private final Map<UUID, SessionShoppingCart> sessions = new ConcurrentHashMap<>();

    @Override
    public SessionShoppingCart getShoppingCart(UUID sessionId) {
        return getOrCreateSessionCart(sessionId);
    }

    /*Agrega el producto al carrito con el productId y quantity*/
    @Override
    public void addProductToCart(UUID sessionId, int productId, int quantity) throws GenericException {
        log.info("addProductToCart productId {} quantity {}", productId, quantity);
        SessionShoppingCart sessionCart = getOrCreateSessionCart(sessionId);
        ProductDto productToAdd = getProductById(productId);
        if(productToAdd == null) {
            throw new GenericException("Not found productId " + productId);
        }
        if (reduceStock(productId, quantity)) {
            Optional<ShoppingCartItemDto> existingItem = sessionCart.getShoppingCartItems().stream()
                    .filter(item -> item.getProductDto().getIdentification() == productId)
                    .findFirst();
            if (existingItem.isPresent()) {
                ShoppingCartItemDto cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                BigDecimal totalPrice = calculateTotalPrice(productToAdd.getImporte(), cartItem.getQuantity());
                cartItem.setTotalPrice(totalPrice);
            } else {
                BigDecimal totalPrice = calculateTotalPrice(productToAdd.getImporte(), quantity);
                sessionCart.getShoppingCartItems().add(new ShoppingCartItemDto(productToAdd, quantity, totalPrice));
            }
            sessionCart.setLastUpdate(LocalDateTime.now());
        } else {
            throw new GenericException("there is no stock "+ quantity + " available for the value sent");
        }
    }

    /*Elimina el producto al carrito con el productId y quantity*/
    @Override
    public void removeProductFromCart(UUID sessionId, int productId, int quantity) throws GenericException {
        log.info("removeProductFromCart productId {} quantity {}", productId, quantity);
        SessionShoppingCart sessionCart = getOrCreateSessionCart(sessionId);
        log.info("removeProductFromCart sessionCart {}", sessionCart);
        Optional<ShoppingCartItemDto> existingItem = sessionCart.getShoppingCartItems().stream()
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
                sessionCart.getShoppingCartItems().remove(existingItem.get());
            }
            sessionCart.setLastUpdate(LocalDateTime.now());
        } else {
            throw new GenericException("quantity of stock "+quantity +" to reduce shipped not available");
        }
    }

    @Override
    public ShoppingCartItemDto getShoppingCartId(UUID sessionId, int id) throws GenericException {
        SessionShoppingCart sessionCart = getOrCreateSessionCart(sessionId);
        return sessionCart.getShoppingCartItems().stream()
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

        if(!sessions.isEmpty()){
            sessions.entrySet().stream().forEach( sessions -> {
                SessionShoppingCart sessionShoppingCart = sessions.getValue();
                UUID sessionUUID = sessions.getKey();
                if (sessionShoppingCart!=null && sessionShoppingCart.getLastUpdate() != null && dateNow.isAfter(sessionShoppingCart.getLastUpdate().plusSeconds(600))) {
                    log.info("sessionShoppingCart {}", sessionShoppingCart);
                    log.info("sessionUUID {}", sessionUUID);
                    log.info("lastUpdate lastUpdate{} dateNow {}", sessionShoppingCart.getLastUpdate(), dateNow);
                    log.info("Clearing shopping cart cache...");
                    List<ShoppingCartItemDto> shoppingCartAux = new ArrayList<>(sessionShoppingCart.getShoppingCartItems());
                    shoppingCartAux.stream().filter(Objects::nonNull).forEach(s-> {
                        try {
                            removeProductFromCart(sessionUUID, s.getProductDto().getIdentification(), s.getQuantity());
                        } catch (GenericException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    sessionShoppingCart.setLastUpdate(null);
                }
            });
        }
    }

    @Override
    public SessionShoppingCart getSessionCart(UUID sessionId) {
        return getOrCreateSessionCart(sessionId);
    }

    private SessionShoppingCart getOrCreateSessionCart(UUID sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new SessionShoppingCart());
    }

}
