package com.svetopolk.demo.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
@Order(1)
@Slf4j
public class LogFilter extends OncePerRequestFilter {

    private final static int maxPayloadLength = 1000;

    private String getContentAsString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) return "";
        int length = Math.min(buf.length, maxPayloadLength);
        try {
            return new String(buf, 0, length, charsetName);
        } catch (UnsupportedEncodingException ex) {
            return "Unsupported Encoding";
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        StringBuilder reqInfo = new StringBuilder()
                .append(request.getMethod())
                .append(" ")
                .append(request.getRequestURL());

        String queryString = request.getQueryString();
        if (queryString != null) {
            reqInfo.append("?").append(queryString);
        }

        log.info("IN " + reqInfo);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        long duration = System.currentTimeMillis() - startTime;

        //log the request's body AFTER the request has been made
        String requestBody = this.getContentAsString(wrappedRequest.getContentAsByteArray(), request.getCharacterEncoding());
        if (requestBody.length() > 0) {
            log.info("IN body:" + requestBody);
        }

        log.info("OUT " + reqInfo + ": status=" + response.getStatus() + " duration(ms)=" + duration);
        log.info("OUT body:" + getContentAsString(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding()));

        wrappedResponse.copyBodyToResponse();
    }
}