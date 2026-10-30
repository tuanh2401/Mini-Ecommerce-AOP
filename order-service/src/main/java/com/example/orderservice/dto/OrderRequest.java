package com.example.orderservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String idempotencyKey; //khóa chống trùng lặp
    private String address;
    private String paymentMethod;
    private List<OrderItemRequest> orderItems;
    private String voucherCode;
}
