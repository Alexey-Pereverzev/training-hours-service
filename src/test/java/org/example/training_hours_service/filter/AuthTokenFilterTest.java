package org.example.training_hours_service.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.example.training_hours_service.jwt.JwtTokenUtil;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void whenDoFilterInternal_validToken_shouldAuthenticateUser() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        Claim roleClaim = mock(Claim.class);
        when(decodedJWT.getSubject()).thenReturn("Aliya.Aliyeva");
        when(decodedJWT.getClaim("role")).thenReturn(roleClaim);
        when(roleClaim.asString()).thenReturn("ROLE_TRAINER");
        when(jwtTokenUtil.validateAndParseToken("valid.jwt.token")).thenReturn(decodedJWT);
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("Aliya.Aliyeva", authentication.getName());
        assertTrue(authentication.getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER")));
        verify(filterChain).doFilter(request, response);
    }


    @Test
    void whenDoFilterInternal_malformedToken_shouldNotAuthenticateUser() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
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
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(200, response.getStatus());
    }


    @Test
    void whenDoFilterInternal_tokenWithoutRole_shouldReturnUnauthorized() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        Claim roleClaim = mock(Claim.class);
        when(decodedJWT.getSubject()).thenReturn("Aliya.Aliyeva");
        when(decodedJWT.getClaim("role")).thenReturn(roleClaim);
        when(roleClaim.asString()).thenReturn(null);
        when(jwtTokenUtil.validateAndParseToken("valid.jwt.token")).thenReturn(decodedJWT);
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void whenDoFilterInternal_invalidJwt_shouldReturnUnauthorized() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bad.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtTokenUtil.validateAndParseToken("bad.jwt.token"))
                .thenThrow(new JWTVerificationException("Signature invalid"));
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }


    @Test
    void whenGetTokenFromJwt_shouldExtractTokenCorrectly() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer my.jwt.token");
        // when + then
        assertEquals("my.jwt.token", authTokenFilter.getTokenFromJwt(request));
    }


    @Test
    void whenGetTokenFromJwt_invalidHeader_shouldReturnNull() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic abc123");
        // when + then
        assertNull(authTokenFilter.getTokenFromJwt(request));
    }

}

