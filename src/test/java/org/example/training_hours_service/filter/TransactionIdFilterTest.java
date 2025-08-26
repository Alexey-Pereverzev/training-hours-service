package org.example.training_hours_service.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;


class TransactionIdFilterTest {

    private final TransactionIdFilter filter = new TransactionIdFilter();


    @Test
    void whenDoFilterInternal_headerExists_thenUseHeaderValue() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(TransactionIdFilter.TX_HEADER, "12345");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> assertEquals("12345", MDC.get("txId"));
        // when
        filter.doFilterInternal(request, response, chain);
        // then
        assertEquals("12345", response.getHeader(TransactionIdFilter.TX_HEADER));
        assertNull(MDC.get("txId"));
    }


    @Test
    void whenDoFilterInternal_headerNotExists_thenGenerateNewId() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        // when
        filter.doFilterInternal(request, response, chain);
        // then
        String txId = response.getHeader(TransactionIdFilter.TX_HEADER);
        assertNotNull(txId);
        assertFalse(txId.isEmpty());
    }

}

