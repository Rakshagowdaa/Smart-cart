package com.smartcart.wishlistservice.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.smartcart.wishlistservice.service.*.*(..))")
    public void logBefore() {
        log.info("Method execution started in Wishlist Service");
    }
}
