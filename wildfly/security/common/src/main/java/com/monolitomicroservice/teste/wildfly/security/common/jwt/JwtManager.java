package com.monolitomicroservice.teste.wildfly.security.common.jwt;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.crypto.MacProvider;

@ApplicationScoped
public class JwtManager {

    private static final String CLAIM_ROLE = "role";

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final SecretKey SECRET_KEY = MacProvider.generateKey(SIGNATURE_ALGORITHM);
    private static final TemporalAmount TOKEN_VALIDITY = Duration.ofHours(4L);

    public static final String AUTH_HEADER_KEY = "Authorization";
    public static final String AUTH_HEADER_VALUE_PREFIX = "Bearer "; // with trailing space to separate token

    public static final int STATUS_CODE_UNAUTHORIZED = 401;

    /**
     * Builds a JWT with the given subject and role and returns it as a JWS signed compact String.
     */
    public String createToken(final String subject, final String role) {
        final Instant now = Instant.now();
        final Date expiryDate = Date.from(now.plus(TOKEN_VALIDITY));
        return Jwts.builder()
                .setSubject(subject)
                .claim(CLAIM_ROLE, role)
                .setExpiration(expiryDate)
                .setIssuedAt(Date.from(now))
                .signWith(SIGNATURE_ALGORITHM, SECRET_KEY)
                .compact();
    }

    /**
     * Parses the given JWS signed compact JWT, returning the claims.
     * If this method returns without throwing an exception, the token can be trusted.
     */
    public Jws<Claims> parseToken(final String compactToken)
            throws ExpiredJwtException,
            UnsupportedJwtException,
            MalformedJwtException,
            SignatureException,
            IllegalArgumentException {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(compactToken);
    }

    /**
     * Get the bearer token from the HTTP request.
     * The token is in the HTTP request "Authorization" header in the form of: "Bearer [token]"
     */
    public static String getBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(JwtManager.AUTH_HEADER_KEY);
        if (authHeader != null && authHeader.startsWith(JwtManager.AUTH_HEADER_VALUE_PREFIX)) {
            return authHeader.substring(JwtManager.AUTH_HEADER_VALUE_PREFIX.length());
        }
        return null;
    }
}
