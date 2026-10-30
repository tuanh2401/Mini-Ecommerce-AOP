package com.example.baseservice.aspect;

import com.example.baseservice.constant.AspectOrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(AspectOrderConstant.NOTIFICATION)
public class NotificationAspect {
    @Before("@annotation(com.example.baseservice.annotation.LogExecution)")
    public void onBeforeOrder(JoinPoint joinPoint){
        log.info("Có khách hàng chuẩn bị đặt đơn mới!");
    }
    @AfterReturning(
            pointcut = "@annotation(com.example.baseservice.annotation.LogExecution)",
            returning = "result"
    )
    public void onOrderSuccess(JoinPoint joinPoint,Object result){
        log.info("Tạo đơn hàng thành công , đang gửi kết quả : {}",result);
    }
    @AfterThrowing(
            pointcut = "@annotation(com.example.baseservice.annotation.LogExecution)",
            throwing = "ex"
    )
    public void onOrderError(JoinPoint joinPoint,Throwable ex){
        log.error("Đơn hàng gặp sự cố , chi tiết lỗi: {}",ex.getMessage());
    }
    @After("@annotation(com.example.baseservice.annotation.LogExecution)")
    public void onOrderFinished(JoinPoint joinPoint){
        log.info("Kết thúc");
    }
}
