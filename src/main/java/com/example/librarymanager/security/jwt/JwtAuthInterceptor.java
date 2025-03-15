package com.example.librarymanager.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;

@Component
public class JwtAuthInterceptor implements HandshakeInterceptor {

    @Value("${jwt.secret:76947ef7-7af1-4745-bfda-ab2d5cb09290}")
    private String SECRET_KEY;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        Optional<String> token = Optional.ofNullable(request.getHeaders().getFirst("Authorization"));

        if (token.isPresent() && token.get().startsWith("Bearer ")) {
            try {
                String jwt = token.get().substring(7);
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(jwt)
                        .getBody();

                attributes.put("userId", claims.getSubject());
                return true;
            } catch (Exception e) {
                throw new Exception("Invalid JWT Token");
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}
