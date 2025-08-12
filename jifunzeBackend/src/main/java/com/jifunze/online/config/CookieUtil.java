package com.jifunze.online.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${jwt.cookie-name:jwt-token}")
    private String cookieName;

    @Value("${jwt.cookie-domain:localhost}")
    private String cookieDomain;

    @Value("${jwt.cookie-path:/}")
    private String cookiePath;

    @Value("${jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${jwt.cookie-http-only:true}")
    private boolean cookieHttpOnly;

    @Value("${jwt.cookie-same-site:Lax}")
    private String cookieSameSite;

    public ResponseCookie createJwtCookie(String token, long maxAge) {
        return ResponseCookie.from(cookieName, token)
                .domain(cookieDomain)
                .path(cookiePath)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .maxAge(maxAge)
                .sameSite(cookieSameSite)
                .build();
    }

    public ResponseCookie createJwtCookie(String token) {
        return createJwtCookie(token, 24 * 60 * 60); // 24 hours default
    }

    public ResponseCookie clearJwtCookie() {
        return ResponseCookie.from(cookieName, "")
                .domain(cookieDomain)
                .path(cookiePath)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .maxAge(0) // Expire immediately
                .sameSite(cookieSameSite)
                .build();
    }

    public String getCookieName() {
        return cookieName;
    }
}
