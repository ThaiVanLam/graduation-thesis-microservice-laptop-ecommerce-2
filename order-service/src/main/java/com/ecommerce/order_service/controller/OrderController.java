package com.ecommerce.order_service.controller;


import com.ecommerce.order_service.payload.OrderDTO;
import com.ecommerce.order_service.payload.OrderRequestDTO;
import com.ecommerce.order_service.payload.StripePaymentDto;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.StripeService;
import com.ecommerce.order_service.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private StripeService stripeService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();
        OrderDTO orderDTO = orderService.placeOrder(emailId, orderRequestDTO.getAddressId(), paymentMethod, orderRequestDTO.getPgName(), orderRequestDTO.getPgPaymentId(), orderRequestDTO.getPgStatus(), orderRequestDTO.getPgResponseMessage());
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDto stripePaymentDto) throws StripeException {
        System.out.println("StripePaymentDTO Received " + stripePaymentDto);
        PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDto);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }
}
