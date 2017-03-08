package com.monolitomicroservice.teste.performancerest.rest;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.monolitomicroservice.teste.performance.service.UserService;
import com.monolitomicroservice.teste.performancerest.persistence.TSTUser;

@Path("/users")
public class UserRest {
    @EJB
    private UserService userService;

    private static Logger log = Logger.getLogger("UserRest");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/find")
    public RestResult find(@QueryParam("start") int start, @QueryParam("size") int size) {
        if (start < 0)
            start = 0;
        if (size <= 0)
            size = 50;

        long ini = System.currentTimeMillis();
        List<TSTUser> l = userService.find(start, size);

        RestResult r = new RestResult(System.currentTimeMillis() - ini, l);

        return r;
    }

    @POST
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public RestResult create(@FormParam("tenantId") Long tenantId,
            @FormParam("userCode") String userCode,
            @FormParam("login") String login,
            @FormParam("password") String password,
            @FormParam("email") String email,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("fullName") String fullName,
            @FormParam("birthDate") Long birthDate) throws Exception {

        long ini = System.currentTimeMillis();
        TSTUser t = userCode == null ? new TSTUser() : new TSTUser(userCode);

        if (tenantId != null) {
            t.setTenantId(tenantId);
        }
        if (login != null) {
            t.setLogin(login);
        }
        if (password != null) {
            t.setPassword(password);
        }
        if (email != null) {
            t.setEmail(email);
        }
        if (firstName != null) {
            t.setFirstName(firstName);
        }
        if (lastName != null) {
            t.setLastName(lastName);
        }
        if (fullName != null) {
            t.setFullName(fullName);
        } else {
            t.setFullName(t.getFirstName() + " " + t.getLastName());
        }
        if (birthDate != null) {
            t.setBirthDate(new Date(birthDate));
        }

        t = userService.create(t);

        RestResult r = new RestResult(System.currentTimeMillis() - ini, t);
        log.info(":::: " + r);
        log.fine("==== " + r);

        return r;
    }
}
