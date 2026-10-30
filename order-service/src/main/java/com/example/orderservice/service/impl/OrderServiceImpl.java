package com.example.orderservice.service.impl;

import com.example.baseservice.annotation.CheckIdempotent;
import com.example.baseservice.annotation.LogExecution;
import com.example.baseservice.annotation.SanitizeOrder;
import com.example.baseservice.annotation.TrackTime;
import com.example.orderservice.dto.OrderItemRequest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.service.OrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    @CheckIdempotent(expireSecond = 10)
    @SanitizeOrder
    @LogExecution(action = "Tạo đơn hàng")
    @TrackTime(threholdMs = 100)
    @SneakyThrows(InterruptedException.class)
    public OrderResponse createOrder(OrderRequest request){
        log.info("Bắt đầu xử lý hàm nghiệp vụ CreateOrder");
        BigDecimal totalAmount = BigDecimal.ZERO;
        if(request.getOrderItems() != null) {
            for(OrderItemRequest item : request.getOrderItems()){
                if(item.getPrice() != null){
                    BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    totalAmount = totalAmount.add(itemTotal);
                }
            }
        }
        BigDecimal discount = BigDecimal.ZERO;
        if("SALE50".equals(request.getVoucherCode())){
            discount = BigDecimal.valueOf(50000);
        }
        BigDecimal finalAmount = totalAmount.subtract(discount);
        if(finalAmount.compareTo(BigDecimal.ZERO) < 0){
            finalAmount = BigDecimal.ZERO;
        }
        if("SLOW".equalsIgnoreCase(request.getPaymentMethod())){
            log.info("Phát hiện PaymentMethod = SLOW : Giả lập nghẽn mạng 300ms");
            Thread.sleep(300);
        }
        return OrderResponse.builder()
                .orderId(UUID.randomUUID().toString())
                .status("CREATED")
                .address(request.getAddress())
                .totalAmount(totalAmount)
                .discountAmount(discount)
                .finalAmount(finalAmount)
                .voucherCode(request.getVoucherCode())
                .build();
    }
}
