package com.ecommerce.order_service.service;


import com.ecommerce.order_service.client.ProductServiceClient;
import com.ecommerce.order_service.exceptions.APIException;
import com.ecommerce.order_service.exceptions.ResourceNotFoundException;
import com.ecommerce.order_service.model.Cart;
import com.ecommerce.order_service.model.CartItem;
import com.ecommerce.order_service.model.ProductSnapshot;
import com.ecommerce.order_service.payload.CartDTO;
import com.ecommerce.order_service.payload.CartItemDTO;
import com.ecommerce.order_service.payload.ProductDTO;
import com.ecommerce.order_service.repositories.CartItemRepository;
import com.ecommerce.order_service.repositories.CartRepository;
import com.ecommerce.order_service.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        ProductDTO product = productServiceClient.getProductById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        CartItem existingItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (existingItem != null) {
            validateInventory(product, quantity, existingItem.getQuantity());
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setDiscount(product.getDiscount());
            existingItem.setProductPrice(product.getSpecialPrice());
            updateSnapshot(existingItem, product);
            cartItemRepository.save(existingItem);
        } else {
            validateInventory(product, quantity, 0);

            CartItem newCartItem = new CartItem();

            newCartItem.setCart(cart);
            newCartItem.setQuantity(quantity);
            newCartItem.setDiscount(product.getDiscount());
            newCartItem.setProductPrice(product.getSpecialPrice());
            newCartItem.setProductSnapshot(mapToSnapshot(product));

            cart.getCartItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }


        double totalPrice = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(totalPrice);

        cartRepository.save(cart);


        return mapToCartDTO(cartRepository.findById(cart.getCartId()).orElse(cart));
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new APIException("No cart exist");
        }

        return carts.stream().map(this::mapToCartDTO).toList();
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        Cart cart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "email", authUtil.loggedInEmail());
        }

        ProductDTO product = productServiceClient.getProductById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!");
        }


        int newQuantity = cartItem.getQuantity() + quantity;

        if (newQuantity < 0) {
            throw new APIException("The resulting quantity cannot be negative.");
        }

        if (newQuantity == 0) {
            deleteProductFromCart(cart.getCartId(), productId);
            return mapToCartDTO(cartRepository.findById(cart.getCartId()).orElse(cart));
        }
        if (quantity > 0) {
            validateInventory(product, quantity, cartItem.getQuantity());
        }

        cartItem.setQuantity(newQuantity);
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount());
        updateSnapshot(cartItem, product);

        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
        cartRepository.save(cart);
        cartItemRepository.save(cartItem);

        return mapToCartDTO(cartRepository.findById(cart.getCartId()).orElse(cart));
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
        return "Product " + cartItem.getProductSnapshot().getProductName() + " removed from the cart!!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));


        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product not available in the cart");
        }


        ProductDTO product = productServiceClient.getProductById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cartItem.setDiscount(product.getDiscount());
        updateSnapshot(cartItem, product);

        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {

        String emailId = authUtil.loggedInEmail();

        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserEmail(emailId);
            cart.setTotalPrice(0.0);
            cart = cartRepository.save(cart);
        } else {
            cartItemRepository.deleteAllByCartId(cart.getCartId());
            cart.getCartItems().clear();
            cart.setTotalPrice(0.0);
        }

        double totalPrice = 0.0;

        for (CartItemDTO cartItemDTO : cartItems) {
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            ProductDTO product = productServiceClient.getProductById(productId);
            if (product == null) {
                throw new ResourceNotFoundException("Product", "productId", productId);
            }

            validateInventory(product, quantity, 0);

            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItem.setProductSnapshot(mapToSnapshot(product));

            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);

            totalPrice += cartItem.getProductPrice() * quantity;
        }

        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
        return "Cart created/updated with the new items successfully";
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUserEmail(authUtil.loggedInEmail());
        return cartRepository.save(cart);
    }

    private CartDTO mapToCartDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setProducts(cart.getCartItems().stream().filter(Objects::nonNull).map(this::mapToProductDTO).collect(Collectors.toList()));
        return cartDTO;
    }

    private ProductDTO mapToProductDTO(CartItem item) {
        ProductSnapshot snapshot = item.getProductSnapshot();
        if (snapshot == null) {
            ProductDTO dto = new ProductDTO();
            dto.setQuantity(item.getQuantity());
            return dto;
        }
        ProductDTO dto = new ProductDTO();
        dto.setProductId(snapshot.getProductId());
        dto.setProductName(snapshot.getProductName());
        dto.setImage(snapshot.getImage());
        dto.setPrice(snapshot.getPrice() == null ? 0.0 : snapshot.getPrice());
        dto.setDiscount(snapshot.getDiscount() == null ? 0.0 : snapshot.getDiscount());
        dto.setSpecialPrice(snapshot.getSpecialPrice() == null ? 0.0 : snapshot.getSpecialPrice());
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    private ProductSnapshot mapToSnapshot(ProductDTO product) {
        return new ProductSnapshot(
                product.getProductId(),
                product.getProductName(),
                product.getImage(),
                product.getPrice(),
                product.getDiscount(),
                product.getSpecialPrice()
        );
    }

    private void updateSnapshot(CartItem cartItem, ProductDTO product) {
        ProductSnapshot snapshot = cartItem.getProductSnapshot();
        if (snapshot == null) {
            cartItem.setProductSnapshot(mapToSnapshot(product));
            return;
        }
        snapshot.setProductName(product.getProductName());
        snapshot.setImage(product.getImage());
        snapshot.setPrice(product.getPrice());
        snapshot.setDiscount(product.getDiscount());
        snapshot.setSpecialPrice(product.getSpecialPrice());
    }

    private void validateInventory(ProductDTO product, Integer quantity, int existingQuantity) {
        if (product.getQuantity() == null || product.getQuantity() <= 0) {
            throw new APIException(product.getProductName() + " is not available");
        }
        if (quantity != null && quantity > 0) {
            int desiredQuantity = existingQuantity + quantity;
            if (desiredQuantity > product.getQuantity()) {
                throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to the quantity " + product.getQuantity() + ".");
            }
        }
    }
}
