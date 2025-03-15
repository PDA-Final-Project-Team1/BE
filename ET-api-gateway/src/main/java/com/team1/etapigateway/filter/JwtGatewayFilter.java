package com.team1.etapigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
@Slf4j
public class JwtGatewayFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final List<String> EXCLUDED_PATHS = List.of("/api/auth/login", "/api/auth/signup", "/api/users/duplicate");

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();

        if (EXCLUDED_PATHS.contains(path)) {
            return chain.filter(exchange);
        }

        String token = getTokenFromRequest(request);

        if (token == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        Claims claims = validateToken(token);
        if (claims == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        String userId = claims.get("X-Id", String.class);
        return authenticateUser(userId)
                .flatMap(securityContext -> chain.filter(exchange.mutate()
                                .request(addCustomHeader(request, "X-Id", userId)) // 헤더에 X-Id 추가
                                .request(addCustomParameter(request, "X-Id", userId)) // 파라미터에 X-Id 추가
                                .build())
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                );
    }

    private String getTokenFromRequest(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String token = authHeaders.get(0);
            if (token.startsWith("Bearer ")) {
                return token.substring(7);
            }
        }
        return null;
    }

    private Claims validateToken(String token) {
        try {
            return parseClaims(token);
        } catch (ExpiredJwtException e) {
            log.error("Token expired");
        } catch (UnsupportedJwtException | SignatureException e) {
            log.error("Invalid token");
        } catch (JwtException e) {
            log.error("Unauthorized");
        }
        return null;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Mono<SecurityContext> authenticateUser(String userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        return Mono.just(new SecurityContextImpl(authentication));
    }

    private ServerHttpRequest addCustomHeader(ServerHttpRequest request, String name, String value) {
        return request.mutate()
                .headers(headers -> headers.set(name, value))
                .build();
    }

    private ServerHttpRequest addCustomParameter(ServerHttpRequest request, String name, String value) {
        // URL 파라미터로 X-Id를 추가합니다.
        String uri = request.getURI().toString();
        String newUri = uri.contains("?") ? uri + "&" + name + "=" + value : uri + "?" + name + "=" + value;
        return request.mutate().uri(URI.create(newUri)).build();
    }
}
