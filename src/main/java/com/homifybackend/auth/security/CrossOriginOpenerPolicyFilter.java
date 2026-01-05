package com.homifybackend.auth.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CrossOriginOpenerPolicyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Add COOP header to allow popup communication with Google OAuth
        // Using "unsafe-none" to allow cross-origin popups to maintain opener reference
        httpResponse.setHeader("Cross-Origin-Opener-Policy", "unsafe-none");
        
        // Add COEP header for compatibility
        httpResponse.setHeader("Cross-Origin-Embedder-Policy", "unsafe-none");

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
