package com.monolitomicroservice.teste.wildfly.security.common.jwt;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter implements Filter {

    private static final java.util.logging.Logger LOG = Logger.getLogger(JwtAuthenticationFilter.class.getSimpleName());
    private static final Level LEVEL = Level.FINEST;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.log(LEVEL, "JwtAuthenticationFilter initialized");
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
            final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        LOG.log(LEVEL, "doFilter - 1");
        boolean loggedIn = false;
        try {
            String jwt = JwtManager.getBearerToken(httpRequest);
            LOG.log(LEVEL, "doFilter - 2 - jwt=" + jwt);

            if (jwt != null && !jwt.isEmpty()) {
                LOG.log(LEVEL, "################## " + jwt + " -- " + httpRequest);
                httpRequest.setAttribute("com.teste.monolitomicroservice.extension.custom", "JWT");
                //httpRequest.login(jwt, jwt);
                loggedIn = true;
                LOG.log(LEVEL, "Logged in using JWT");
            } else {
                LOG.log(LEVEL, "No JWT provided, go on unauthenticated");
            }

            LOG.log(LEVEL, "doFilter - 3 - loggedIn=" + loggedIn);
            String token = jwt != null ? jwt : (String) httpRequest.getAttribute("_jwt_token_");
            LOG.log(LEVEL, "doFilter - 4 - _jwt_token_=" + token);

            if (token != null) {
                LOG.log(LEVEL, "doFilter - 5 - setando header");
                httpResponse.setHeader(JwtManager.AUTH_HEADER_KEY, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
            }

            filterChain.doFilter(servletRequest, servletResponse);
            LOG.log(LEVEL, "doFilter - 6");

            token = jwt != null ? jwt : (String) httpRequest.getAttribute("_jwt_token_");
            LOG.log(LEVEL, "doFilter - 7 - _jwt_token_=" + token);
            if (token != null) {
                LOG.log(LEVEL, "doFilter - 8 - setando header");
                httpResponse.setHeader(JwtManager.AUTH_HEADER_KEY, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
            }

            if (loggedIn) {
                httpRequest.logout();
                LOG.log(LEVEL, "Logged out");
            }
            LOG.log(LEVEL, "doFilter - 9 - FIM");
        } catch (final Exception e) {
            LOG.log(Level.WARNING, "Failed logging in with security token", e);
            httpResponse.setContentLength(0);
            httpResponse.setStatus(JwtManager.STATUS_CODE_UNAUTHORIZED);
        }
        LOG.log(LEVEL, "=========================================================================");
    }

    @Override
    public void destroy() {
        LOG.log(LEVEL, "JwtAuthenticationFilter destroyed");
    }
}
