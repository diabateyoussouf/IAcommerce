package net.youssouf.backend.controllers;

import lombok.RequiredArgsConstructor;
import net.youssouf.backend.dtos.AddToCartRequest;
import net.youssouf.backend.dtos.QuantityDTO;
import net.youssouf.backend.entities.AppUser;
import net.youssouf.backend.entities.Cart;
import net.youssouf.backend.repositories.CartRepository;
import net.youssouf.backend.securities.CartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "${app.frontend.url}")
@RequiredArgsConstructor
public class RestCartController {

    private final CartRepository cartRepository;
    private final CartService cartService;


    @GetMapping
    public Page<Cart> getCart(
            @RequestParam(required = false) String sessionId,
            Pageable pageable
    ) {
        AppUser user = getAuthenticatedUserOrNull();

        if (user != null) {
            return cartRepository.findByUser_Id(user.getId(), pageable);
        } else if (sessionId != null) {
            return cartRepository.findBySessionId(sessionId, pageable);
        } else {
            return Page.empty();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody AddToCartRequest request) {
        AppUser user = getAuthenticatedUserOrNull();
        Cart updatedCart = cartService.addProductToCart(user, request.getSessionId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/item/{itemId}")
    public ResponseEntity<Void> updateCartItemQuantity(@PathVariable Long itemId, @RequestBody QuantityDTO dto) {
        cartService.updateItemQuantity(itemId, dto.getQuantity());
        return ResponseEntity.ok().build();
    }

    // return cart by id
    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        Optional<Cart> cart = cartRepository.findById(id);
        return cart.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    private AppUser getAuthenticatedUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof AppUser user) {
            return user;
        }
        return null;
    }

}
