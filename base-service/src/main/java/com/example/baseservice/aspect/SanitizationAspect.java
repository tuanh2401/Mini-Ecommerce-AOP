package com.example.baseservice.aspect;

import com.example.baseservice.annotation.SanitizeOrder;
import com.example.baseservice.constant.AspectOrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Aspect
@Component
@Order(AspectOrderConstant.SANITIZATION)
public class SanitizationAspect {
    @Around("@annotation(sanitizeOrder)")
    public Object sanitizeInput(ProceedingJoinPoint joinPoint, SanitizeOrder sanitizeOrder) throws Throwable{
        Object[] args = joinPoint.getArgs();
        if(args != null && args.length > 0){
            for(Object arg : args) {
                if(arg != null){
                    sanitizeObject(arg);
                }
            }
        }
        return joinPoint.proceed(args);
    }
    public void sanitizeObject(Object obj){
        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field field : fields){
            if(field.getType().equals(String.class)){
                try {
                    field.setAccessible(true);
                    String value = (String) field.get(obj);
                    if(value != null){
                        String cleanedValue = value.trim();
                        if("voucherCode".equalsIgnoreCase(field.getName())){
                            cleanedValue = cleanedValue.toUpperCase();
                        }
                        field.set(obj,cleanedValue);
                    }
                }catch (IllegalAccessException e){
                    log.error("Không thể can thiệp vào thuộc tính: {}",field.getName(),e);
                }
            }
        }
    }
}
