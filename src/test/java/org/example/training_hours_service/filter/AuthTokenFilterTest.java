package org.example.training_hours_service.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.example.training_hours_service.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;


    @Test
    void whenDoFilterInternal_validToken_shouldAuthenticateUser() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtTokenUtil.getUsername("valid.jwt.token")).thenReturn("Aliya.Aliyeva");
        when(jwtTokenUtil.getRole("valid.jwt.token")).thenReturn("ROLE_TRAINER");
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("Aliya.Aliyeva", authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER")));
    }

    @Test
    void whenDoFilterInternal_tokenInvalid_shouldNotAuthenticateUser() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtTokenUtil.getUsername("invalid.token"))
                .thenThrow(new JWTVerificationException("Invalid signature"));
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void whenDoFilterInternal_noToken_shouldJustContinue() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }
}

