package com.monolitomicroservice.teste.performancerest.rest;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;

import com.monolitomicroservice.teste.performance.service.UserService;
import com.monolitomicroservice.teste.performancerest.persistence.TSTUser;

@Path("/users")
public class UserRest {
    private static final String appName = "";
    private static final String moduleName = "PerformanceServer";
    private static final String distinctName = "";
    private static final String beanName = "UserService";
    private static final String interfaceFullName = "com.monolitomicroservice.teste.performance.service.UserService";
    private static final String jndiName = "ejb:" + appName + "/" + moduleName + "/"
            + distinctName + "/" + beanName + "!" + interfaceFullName;
    private static final boolean balanced;

    private static final Logger log = Logger.getLogger(UserRest.class.getName());

    static {
        balanced = System.getenv("BALANCED") != null && System.getenv("BALANCED").equals("true");

        Properties clientProperties = new Properties();
        clientProperties
                .put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED",
                        "false");
        clientProperties.put("remote.connections", "default");
        clientProperties.put("remote.connection.default.port", balanced ? "8081" : "8080");
        clientProperties.put("remote.connection.default.host", balanced ? "PerformanceHA" : "PerformanceServer");

        //clientProperties.put("remote.connection.default.username", "eder");
        //clientProperties.put("remote.connection.default.password", "@eder1");
        clientProperties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

        EJBClientContext.setSelector(new ConfigBasedEJBClientContextSelector(new PropertiesBasedEJBClientConfiguration(clientProperties)));
    }

    private static UserService userService = null;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/find")
    public RestResult find(@QueryParam("start") int start, @QueryParam("size") int size,
            @QueryParam("cached") @DefaultValue("false") String cached) throws Exception {
        long ini = System.currentTimeMillis();

        if (start < 0)
            start = 0;
        if (size <= 0)
            size = 50;

        UserService service = cached.equals("true") ? locateCachedEJB() : locateEJB();

        List<TSTUser> l = service.find(start, size);

        RestResult r = new RestResult(System.currentTimeMillis() - ini, l);
        //log.info("==== Users found: " + l.size());

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
            @FormParam("birthDate") Long birthDate,
            @FormParam("cached") @DefaultValue("false") String cached) throws Exception {

        long ini = System.currentTimeMillis();

        UserService service = cached.equals("true") ? locateCachedEJB() : locateEJB();

        TSTUser user = new TSTUser();
        user = service.create(user);

        RestResult r = new RestResult(System.currentTimeMillis() - ini, user);
        //log.info("==== User created: " + user);

        return r;
    }

    public static UserService locateCachedEJB() throws NamingException {
        if (userService == null) {
            userService = locateEJB();
        }
        return userService;
    }

    public static <T> T locateEJB() throws NamingException {
        Properties properties = new Properties();
        properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        //TODO: Teste
        //properties.put("org.jboss.ejb.client.scoped.context","true");
        Context context = new InitialContext(properties);
        return (T) context.lookup(jndiName);
    }
}
