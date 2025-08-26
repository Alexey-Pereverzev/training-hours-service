package org.example.training_hours_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)                                      // after TransactionIdFilter
public class RestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RestLoggingFilter.class);
    private static final int MAX_LOG_BYTES = 4096;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(res);
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long tookMs = System.currentTimeMillis() - start;

            String uri = req.getRequestURI();
            String method = req.getMethod();
            String query = StringUtils.hasText(req.getQueryString()) ? "?" + req.getQueryString() : "";
            String reqBody = body(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
            String resBody = body(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());

            int status = responseWrapper.getStatus();
            if (status < 400) {
                log.info("REST {} {}{} -> {} ({} ms)\nREQ:{}\nRES:{}",
                        method, uri, query, status, tookMs, reqBody, resBody);
            } else {
                log.warn("REST {} {}{} -> {} ({} ms)\nREQ:{}\nRES:{}",
                        method, uri, query, status, tookMs, reqBody, resBody);
            }
            responseWrapper.copyBodyToResponse();           // return body to client
        }
    }


    private String body(byte[] buf, String enc) {
        if (buf == null || buf.length == 0) return "<empty>";
        int len = Math.min(buf.length, MAX_LOG_BYTES);
        try {
            return new String(buf, 0, len, enc);
        } catch (Exception e) {
            return "<binary:" + len + " bytes>";
        }
    }
}
