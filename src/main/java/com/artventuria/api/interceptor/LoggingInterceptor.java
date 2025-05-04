package com.artventuria.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();
        String method = request.getMethod();
        String path = request.getRequestURI();
        String clientIp = request.getRemoteAddr();
        
        MDC.clear(); // Clear any existing MDC values
        MDC.put("requestId", requestId);
        MDC.put("method", method);
        MDC.put("path", path);
        MDC.put("clientIp", clientIp);
        
        request.setAttribute("startTime", System.currentTimeMillis());
        
        logger.info("Incoming request - Method: {} | Path: {} | Client IP: {}",
                method,
                path,
                clientIp);
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        
        MDC.put("status", String.valueOf(response.getStatus()));
        MDC.put("duration", duration + "ms");
        
        logger.info("REST Response - Status: {} | Duration: {}ms | Method: {} | Path: {}",
                response.getStatus(),
                duration,
                request.getMethod(),
                request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.clear();
    }
}