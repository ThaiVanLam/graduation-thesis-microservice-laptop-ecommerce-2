package com.ecommerce.order_service.controller;


import com.ecommerce.order_service.config.AppConstants;
import com.ecommerce.order_service.payload.*;
import com.ecommerce.order_service.service.AnalyticsService;
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

    @Autowired
    private AnalyticsService analyticsService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();
        System.out.println("orderRequestDTO DATA: " + orderRequestDTO);
        OrderDTO orderDTO = orderService.placeOrder(emailId, orderRequestDTO.getAddressId(), paymentMethod, orderRequestDTO.getPgName(), orderRequestDTO.getPgPaymentId(), orderRequestDTO.getPgStatus(), orderRequestDTO.getPgResponseMessage());
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDto stripePaymentDto) throws StripeException {
        System.out.println("StripePaymentDTO Received " + stripePaymentDto);
        PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDto);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }

    @GetMapping("/admin/app/analytics")
    public ResponseEntity<AnalyticsOrderResponse> getAnalytics() {
        AnalyticsOrderResponse response = analyticsService.getAnalyticsData();
        return new ResponseEntity<AnalyticsOrderResponse>(response, HttpStatus.OK);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }
}
