package com.monolitomicroservice.teste.wildfly.security.loginmodule;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import com.monolitomicroservice.teste.wildfly.security.common.SecurityConstants;

import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpServletRequestImpl;

public class CustomHttpHandler implements HttpHandler {
    protected static Level LEVEL = Level.INFO;
    private static final Logger LOG = Logger.getLogger(CustomHttpHandler.class.getSimpleName());

    private HttpHandler next;

    public CustomHttpHandler(HttpHandler next) {
        LOG.log(LEVEL, "CustomHttpHandler(" + next + ")");
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        LOG.log(LEVEL, "handleRequest(" + exchange + ")");

        exchange.addExchangeCompleteListener(new EndRequestListener());
        next.handleRequest(exchange);

        LOG.log(LEVEL, "handleRequest - FIM");
    }

    private class EndRequestListener implements ExchangeCompletionListener {
        @Override
        public void exchangeEvent(HttpServerExchange exchange, NextListener nextListener) {
            LOG.log(LEVEL, "exchangeEvent(" + exchange + ", " + nextListener + ")");

            try {
                ServletRequestContext requestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
                if (requestContext != null) {
                    HttpServletRequestImpl request = requestContext.getOriginalRequest();
                    String needLogout = (String) request.getAttribute(SecurityConstants.LOGOUT_REQUIRED_ATTRIBUTE);
                    if (needLogout != null && needLogout.equals(Boolean.TRUE.toString())) {
                        try {
                            request.logout();
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.invalidate();
                            }
                        } catch (ServletException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                if (nextListener != null) {
                    nextListener.proceed();
                }
            }

            LOG.log(LEVEL, "exchangeEvent() - FIM");
        }
    }
}
