package com.khanh.fintech_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtFilterTest {

    @Test
    void allowsAuthPathWithoutHeaders() throws ServletException, IOException {
        var filter = new JwtFilter();
        var req = new MockHttpServletRequest("POST", "/auth/login");
        var resp = new MockHttpServletResponse();
        FilterChain chain = (request, response) -> { };
        filter.doFilter(req, resp, chain);
        assertEquals(null, resp.getHeader("X-User-Id"));
    }

    @Test
    void injectsHeadersWhenBearerPresent() throws ServletException, IOException {
        var filter = new JwtFilter();
        var req = new MockHttpServletRequest("GET", "/accounts/1");
        req.addHeader("Authorization", "Bearer token");
        var resp = new MockHttpServletResponse();
        FilterChain chain = (request, response) -> { };
        filter.doFilter(req, resp, chain);
        assertEquals("demo-user", resp.getHeader("X-User-Id"));
        assertEquals("ROLE_OPERATOR", resp.getHeader("X-Roles"));
    }
}

