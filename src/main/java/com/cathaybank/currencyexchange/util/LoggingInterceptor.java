package com.cathaybank.currencyexchange.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.UUID;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID = "REQUEST_ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ID, requestId);

        if (request instanceof ContentCachingRequestWrapper) {
            logRequest(requestId, (ContentCachingRequestWrapper) request);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // No action needed
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestId = (String) request.getAttribute(REQUEST_ID);

        if (response instanceof ContentCachingResponseWrapper) {
            logResponse(requestId, (ContentCachingResponseWrapper) response);
        }
    }

    private void logRequest(String requestId, ContentCachingRequestWrapper request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n============================= REQUEST =============================\n");
        logMessage.append("ID: ").append(requestId).append("\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Headers: \n");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logMessage.append("  ").append(headerName).append(": ")
                    .append(request.getHeader(headerName)).append("\n");
        }

        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            logMessage.append("Body: \n")
                    .append(new String(content, StandardCharsets.UTF_8)).append("\n");
        }

        logMessage.append("==================================================================\n");
        log.debug(logMessage.toString());
    }

    private void logResponse(String requestId, ContentCachingResponseWrapper response) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n============================= RESPONSE ============================\n");
        logMessage.append("ID: ").append(requestId).append("\n");
        logMessage.append("Status: ").append(response.getStatus()).append("\n");
        logMessage.append("Headers: \n");

        for (String headerName : response.getHeaderNames()) {
            logMessage.append("  ").append(headerName).append(": ")
                    .append(response.getHeader(headerName)).append("\n");
        }

        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            logMessage.append("Body: \n")
                    .append(new String(content, StandardCharsets.UTF_8)).append("\n");
        }

        logMessage.append("==================================================================\n");
        log.debug(logMessage.toString());
    }
}