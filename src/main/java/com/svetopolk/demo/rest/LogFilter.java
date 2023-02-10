package com.svetopolk.demo.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.springframework.util.Assert.notEmpty;

@Component
@Slf4j
public class LogFilter extends OncePerRequestFilter {

    private final static int BODY_LOG_LIMIT = 1000;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

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

        //refactor: bad idea to log request after it's execution, but ContentCachingResponseWrapper does not allow to do it earlier
        String requestBody = this.getContentAsString(wrappedRequest.getContentAsByteArray(), request.getCharacterEncoding());
        if (requestBody.length() > 0) {
            log.info("IN body:" + requestBody);
        }

        log.info("OUT " + reqInfo + ": status=" + response.getStatus() + " duration(ms)=" + duration);
        String content = getContentAsString(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding());
        if (StringUtils.hasText(content)) {
            log.info("OUT body:" + content, response.getCharacterEncoding());
        }

        wrappedResponse.copyBodyToResponse();
    }

    private String getContentAsString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) return "";
        int length = Math.min(buf.length, BODY_LOG_LIMIT);
        try {
            return new String(buf, 0, length, charsetName);
        } catch (UnsupportedEncodingException ex) {
            return "Unsupported Encoding";
        }
    }
}