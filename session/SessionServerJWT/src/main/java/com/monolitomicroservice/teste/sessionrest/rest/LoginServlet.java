package com.monolitomicroservice.teste.sessionrest.rest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "login", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(LoginServlet.class.getName());
    protected static final Level LEVEL = Level.FINEST;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        execute(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        execute(req, resp);
    }

    private void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("j_username");
        if (username == null)
            req.getAttribute("j_username");
        String passord = req.getParameter("j_password");
        if (passord == null)
            req.getAttribute("j_password");
        LOG.log(LEVEL, "::::::::: execute (" + username + ", " + passord + ")");
        req.login(username, passord);
    }
}
