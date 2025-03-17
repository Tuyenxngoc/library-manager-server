package com.example.librarymanager.security.jwt;

import com.example.librarymanager.service.CustomUserDetailsService;
import com.example.librarymanager.service.JwtBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider tokenProvider;

    private final JwtBlacklistService tokenService;

    private final CustomUserDetailsService customUserDetailsService;

    private void setAuthentication(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String jwt = null;

        try {
            if (request instanceof ServletServerHttpRequest) {
                HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

                String authHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    jwt = authHeader.substring(7);
                }

                if (jwt == null) {
                    jwt = servletRequest.getParameter("token");
                }
            }

            if (jwt == null || !tokenProvider.validateToken(jwt) || !tokenProvider.isAccessToken(jwt) || tokenService.isTokenBlocked(jwt)) {
                return false;
            }

            String userId = tokenProvider.extractSubjectFromJwt(jwt);
            String cardNumber = (userId == null) ? tokenProvider.extractClaimCardNumber(jwt) : null;

            if (userId != null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUserId(userId);
                setAuthentication(userDetails);
            } else if (cardNumber != null) {
                UserDetails userDetails = customUserDetailsService.loadUserByCardNumber(cardNumber);
                setAuthentication(userDetails);
            }

            return true;
        } catch (Exception e) {
            log.error("Invalid JWT Token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}
