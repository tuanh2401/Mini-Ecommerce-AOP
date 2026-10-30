package com.example.baseservice.aspect;

import com.example.baseservice.annotation.LogExecution;
import com.example.baseservice.constant.AspectOrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@Order(AspectOrderConstant.LOGGING)
public class LoggingAspect {
    @Around("@annotation(logExecution)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String actionName = logExecution.action().isEmpty() ? methodName : logExecution.action();
        Object[] args = joinPoint.getArgs();
        log.info("Bắt đầu: {} - Input tham số: {}",actionName, Arrays.toString(args));
        try{
            Object result = joinPoint.proceed();
            log.info("Kết thúc: {} - Output kết quả: {}",actionName,result);
            return result;
        }catch (Throwable ex){
            log.error("Lỗi {} - Thông tin lỗi: {}",actionName,ex.getMessage());
            throw ex;
        }
    }
}
