package com.example.baseservice.aspect;

import com.example.baseservice.annotation.TrackTime;
import com.example.baseservice.constant.AspectOrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(AspectOrderConstant.PERFORMANCE)
public class PerformanceAspect {
    @Around("@annotation(trackTime)")
    public Object trackExecutionTime(ProceedingJoinPoint joinPoint, TrackTime trackTime) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;
        long threshold = trackTime.threholdMs();
        String methodName = joinPoint.getSignature().toShortString();
        if(executionTime > threshold){
            log.warn("[Cảnh báo chậm] Hàm {} tốn {} (vượt quá ngưỡng cho phép {} ms)!",methodName,executionTime,threshold);
        }else{
            log.info("[Hiệu năng tốt] Hàm {} chạy hoàn tất trong {} ms",methodName,executionTime);
        }
        return result;
    }
}
