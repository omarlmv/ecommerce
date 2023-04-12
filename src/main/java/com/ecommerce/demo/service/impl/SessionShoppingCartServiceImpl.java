package com.ecommerce.demo.service.impl;

import com.ecommerce.demo.dto.ShoppingCartItemDto;
import com.ecommerce.demo.exception.GenericException;
import com.ecommerce.demo.service.SessionShoppingCartService;
import com.ecommerce.demo.service.ShoppingCartService;
import com.ecommerce.demo.session.SessionShoppingCart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class SessionShoppingCartServiceImpl implements SessionShoppingCartService {

    private final ShoppingCartService shoppingCartService;

    @Override
    public SessionShoppingCart getShoppingCart(HttpServletRequest request) throws GenericException {
        UUID sessionId = getSessionId(request);
        return shoppingCartService.getShoppingCart(sessionId);
    }

    @Override
    public ShoppingCartItemDto getShoppingCartId(HttpServletRequest request, int productId) throws GenericException {
        UUID sessionId = getSessionId(request);
        return shoppingCartService.getShoppingCartId(sessionId, productId);
    }

    @Override
    public void addProductToCart(HttpServletRequest request, int productId, int quantity) throws GenericException {
        UUID sessionId = getSessionId(request);
        shoppingCartService.addProductToCart(sessionId, productId, quantity);
    }

    @Override
    public void removeProductFromCart(HttpServletRequest request, int productId, int quantity) throws GenericException {
        UUID sessionId = getSessionId(request);
        shoppingCartService.removeProductFromCart(sessionId, productId, quantity);
    }

    private UUID getSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        UUID sessionId = (UUID) session.getAttribute("cartSessionId");
        if (sessionId == null) {
            sessionId = UUID.randomUUID();
            session.setAttribute("cartSessionId", sessionId);
        }
        log.info("getSessionId {}", sessionId);
        return sessionId;
    }

}

