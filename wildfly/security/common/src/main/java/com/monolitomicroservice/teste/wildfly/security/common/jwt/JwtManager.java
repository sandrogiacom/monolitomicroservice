package com.monolitomicroservice.teste.wildfly.security.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

@ApplicationScoped
public class JwtManager {

    private static final String CLAIM_ROLE = "role";

    private static final byte[] ENCODED = new byte[]{-108, -32, -18, -25, -19, 81, -44, 109, 41, -82, 107, 39, -121,
            88, 87, 97, 118, 51, 92, -40, -60, -35, -53, -37, -27, 51, 84, 127, -58, 13, -65, -112};

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    //private static final SecretKey SECRET_KEY = MacProvider.generateKey(SIGNATURE_ALGORITHM);
    private static final SecretKey SECRET_KEY = new SecretKeySpec(ENCODED, "SecretKeySpec");
    /*
    private static final SecretKey SECRET_KEY = new SecretKey() {
        @Override
        public String getAlgorithm() {
            return "HmacSHA256";
        }

        @Override
        public String getFormat() {
            return "RAW";
        }

        @Override
        public byte[] getEncoded() {
            return ENCODED;
        }
    }
    */
    private static final TemporalAmount TOKEN_VALIDITY = Duration.ofHours(4L);

    public static final String AUTH_HEADER_KEY = "Authorization";
    public static final String AUTH_HEADER_VALUE_PREFIX = "Bearer "; // with trailing space to separate token

    public static final int STATUS_CODE_UNAUTHORIZED = 401;

    /**
     * Builds a JWT with the given subject and role and returns it as a JWS signed compact String.
     */
    public String createToken(final String subject, final String role) {
        System.out.println("********************** createToken(subject=" + subject + ", role=" + role + ")");
        System.out.println("********************** createToken - SecretKey=" + SECRET_KEY + " -> " + SECRET_KEY.getAlgorithm()
                + " : " + SECRET_KEY.getFormat() + " : " + SECRET_KEY.getEncoded());
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
        System.out.println("********************** parseToken(compactToken=" + compactToken + ")");
        System.out.println("********************** parseToken - SecretKey=" + SECRET_KEY + " -> " + SECRET_KEY.getAlgorithm()
                + " : " + SECRET_KEY.getFormat() + " : " + SECRET_KEY.getEncoded());
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
