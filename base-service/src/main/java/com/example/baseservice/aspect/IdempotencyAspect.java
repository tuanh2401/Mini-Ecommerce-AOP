package com.example.baseservice.aspect;

import com.example.baseservice.annotation.CheckIdempotent;
import com.example.baseservice.constant.AspectOrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
@Order(AspectOrderConstant.IDEMPOTENCY)
public class IdempotencyAspect {
    private final Map<String, Long> keyStorege = new ConcurrentHashMap<>();
    @Around("@annotation(checkIdempotent)")
    public Object checkDuplicate(ProceedingJoinPoint joinPoint, CheckIdempotent checkIdempotent) throws Throwable{
        String idempotencyKey = extractKey(joinPoint.getArgs());
        if(idempotencyKey != null && !idempotencyKey.trim().isEmpty()){
            long currentTime = System.currentTimeMillis();
            long expireTimeMs = checkIdempotent.expireSecond() * 1000;
            if(keyStorege.containsKey(idempotencyKey)){
                long lasRequestTime = keyStorege.get(idempotencyKey);
                if(currentTime - lasRequestTime < expireTimeMs){
                    log.warn("Cảnh báo - Phát hiện request trùng lặp với Key: {}",idempotencyKey);
                    throw new IllegalStateException("Yêu cầu của bạn đang được xử lý . Vui lòng không thao tác quá nhanh!");
                }
            }
            keyStorege.put(idempotencyKey, currentTime);
        }
        return joinPoint.proceed();
    }
    private String extractKey(Object[] args) {
        if(args == null) return null;
        for(Object arg : args){
            if(arg != null){
                try{
                    Field field = arg.getClass().getDeclaredField("idempotencyKey");
                    field.setAccessible(true);
                    Object value = field.get(arg);
                    if(value != null){
                        return value.toString();
                    }
                }catch (NoSuchFieldException ignored){
                }catch (Exception e){
                    log.error("Lỗi khi đọc idempotencyKey: ",e);
                }
            }
        }
        return null;
    }

}
