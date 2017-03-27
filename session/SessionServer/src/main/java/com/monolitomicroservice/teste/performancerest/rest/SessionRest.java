package com.monolitomicroservice.teste.performancerest.rest;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/session")
public class SessionRest {
    private static Logger log = Logger.getLogger("UserRest");

    @Context
    private HttpServletRequest httpRequest;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public RestResult get(@QueryParam("key") String key) {
        long init = System.currentTimeMillis();

        RestResult r = new RestResult(httpRequest.getSession().getId());
        r.setContent(httpRequest.getSession().getAttribute(key));
        r.setContainer(System.getProperty("jboss.qualified.host.name"));
        r.setElapsedTime(System.currentTimeMillis() - init);

        log.fine("==== get: " + r);
        return r;
    }

    @POST
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/set")
    public RestResult set(@FormParam("key") String key,
            @FormParam("value") String value) throws Exception {

        long init = System.currentTimeMillis();

        RestResult r = new RestResult(httpRequest.getSession().getId());
        httpRequest.getSession().setAttribute(key, value);
        r.setContent(value);
        r.setContainer(System.getProperty("jboss.qualified.host.name"));
        r.setElapsedTime(System.currentTimeMillis() - init);
        log.fine("==== set: " + r);

        return r;
    }
}
