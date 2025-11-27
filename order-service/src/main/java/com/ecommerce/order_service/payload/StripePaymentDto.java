package com.ecommerce.order_service.payload;


import lombok.Data;

import java.util.Map;

@Data
public class StripePaymentDto {
    private Long amount;
    private String currency;
}
