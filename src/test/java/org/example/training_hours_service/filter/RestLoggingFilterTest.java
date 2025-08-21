package org.example.training_hours_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;


class RestLoggingFilterTest {

    private final RestLoggingFilter filter = new RestLoggingFilter();

    @Test
    void whenDoFilterInternal_requestProcessed_thenResponseBodyReturned() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
        request.setContent("hello".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
            res.setContentType("text/plain");
            res.getWriter().write("world");
        };
        // when
        filter.doFilterInternal(request, response, chain);
        // then
        String body = response.getContentAsString();
        assertEquals("world", body);
    }

    @Test
    void whenDoFilterInternal_responseError_thenStillLogsAndReturnsBody() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/error");
        request.setContent("data".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> ((HttpServletResponse) res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request");
        // when
        filter.doFilterInternal(request, response, chain);
        // then
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }
}

