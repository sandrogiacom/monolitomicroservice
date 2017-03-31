package com.monolitomicroservice.teste.sessionrest.rest;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;

import com.monolitomicroservice.teste.session.service.UserService;

@Path("/p/session")
public class ProtectedSessionRest {
    /*
    private static final String appName = "sessionserverejb";
    private static final String moduleName = "sessionrest";
    private static final String distinctName = "";
    private static final String beanName = "UserServiceImpl";
    private static final String interfaceFullName = UserService.class.getName();
    private static final String jndiName = "ejb:" + appName + "/" + moduleName + "/"
            + distinctName + "/" + beanName + "!" + interfaceFullName;
    */

    private static Logger log = Logger.getLogger("ProtectedSessionRest");

    @Context
    private HttpServletRequest httpRequest;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public RestResult get(@QueryParam("key") String key) {
        long init = System.currentTimeMillis();
        log.fine("==== get: key=" + key);

        RestResult r = new RestResult(httpRequest.getSession().getId());
        r.setContent(httpRequest.getSession().getAttribute(key));
        r.setContainer(System.getProperty("jboss.qualified.host.name"));
        r.setElapsedTime(System.currentTimeMillis() - init);
        r.setLogin(httpRequest.getUserPrincipal() != null ? httpRequest.getUserPrincipal().getName() : null);

        log.fine("==== get: " + r);
        return r;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/set")
    public RestResult set(@FormParam("key") String key,
            @FormParam("value") String value) throws Exception {
        long init = System.currentTimeMillis();
        log.fine("==== set: key=" + key + ", value=" + value);

        RestResult r = new RestResult(httpRequest.getSession().getId());
        httpRequest.getSession().setAttribute(key, value);
        r.setContent(value);
        r.setContainer(System.getProperty("jboss.qualified.host.name"));
        r.setElapsedTime(System.currentTimeMillis() - init);
        r.setLogin(httpRequest.getUserPrincipal() != null ? httpRequest.getUserPrincipal().getName() : null);
        log.fine("==== set: " + r);

        return r;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/put")
    public RestResult put(@QueryParam("key") String key,
            @QueryParam("value") String value) throws Exception {
        long init = System.currentTimeMillis();
        log.fine("==== put: key=" + key + ", value=" + value);

        RestResult r = new RestResult(httpRequest.getSession().getId());
        httpRequest.getSession().setAttribute(key, value);
        r.setContent(value);
        r.setContainer(System.getProperty("jboss.qualified.host.name"));
        r.setElapsedTime(System.currentTimeMillis() - init);
        r.setLogin(httpRequest.getUserPrincipal() != null ? httpRequest.getUserPrincipal().getName() : null);
        log.fine("==== set: " + r);

        return r;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user")
    public RestResult getCurrentUser() throws Exception {
        long init = System.currentTimeMillis();
        log.fine("==== getCurrentUser");

        RestResult r = new RestResult(httpRequest.getSession().getId());
        UserService userService = locateEJB();
        System.out.println(":::::: userService=" + userService + " -- " + userService.getClass()
                + " -- " + (userService instanceof UserService));
        r.setContent(userService.getCurrentUser());
        r.setContainer(System.getProperty("jboss.qualified.host.name"));
        r.setElapsedTime(System.currentTimeMillis() - init);
        r.setLogin(httpRequest.getUserPrincipal() != null ? httpRequest.getUserPrincipal().getName() : null);

        log.fine("==== get: " + r);
        return r;
    }

    private static UserService locateEJB() throws NamingException {
        //INFO: Usando remote-naming
        Properties properties = new Properties();
        properties.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        properties.put(javax.naming.Context.PROVIDER_URL, "http-remoting://sessionejbserver:8080");
        properties.put(javax.naming.Context.SECURITY_PRINCIPAL, "ejb");
        properties.put(javax.naming.Context.SECURITY_CREDENTIALS, "test");
        properties.put("jboss.naming.client.ejb.context", true);
        properties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");

        log.info("######## vai criar context");
        javax.naming.Context context = new InitialContext(properties);
        log.info("######## criou context");

        //traverseJndiNode("/", context);

        try {
            log.info("######## vai fazer lookup");
            UserService service = (UserService) context.lookup("sessionserverejb/sessionrest/UserServiceImpl!com.monolitomicroservice.teste.session.service.UserService");
            log.info("@@@@@@@@@ achou 1");
            //System.out.println("########### " + service.getCurrentUser());
            return service;
        } catch (Exception ex1) {
            System.out.println("EEEEEEEEE Erro 1: " + ex1.getMessage());
            try {
                UserService service = (UserService) context.lookup("java:global/sessionserverejb/sessionrest/UserServiceImpl!com.monolitomicroservice.teste.session.service.UserService");
                System.out.println("@@@@@@@@@ achou 2");
                System.out.println("########### " + service.getCurrentUser());
                return service;
            } catch (Exception ex2) {
                System.out.println("EEEEEEEEE Erro 2: " + ex2.getMessage());
                try {
                    UserService service = (UserService) context.lookup("java:app/sessionrest/UserServiceImpl!com.monolitomicroservice.teste.session.service.UserService");
                    System.out.println("@@@@@@@@@ achou 3");
                    System.out.println("########### " + service.getCurrentUser());
                    return service;
                } catch (Exception ex3) {
                    System.out.println("EEEEEEEEE Erro 3: " + ex3.getMessage());
                    try {
                        UserService service = (UserService) context.lookup("java:module/UserServiceImpl!com.monolitomicroservice.teste.session.service.UserService");
                        System.out.println("@@@@@@@@@ achou 4");
                        System.out.println("########### " + service.getCurrentUser());
                        return service;
                    } catch (Exception ex4) {
                        System.out.println("EEEEEEEEE Erro 4: " + ex4.getMessage());
                        try {
                            UserService service = (UserService) context.lookup("java:jboss/exported/sessionserverejb/sessionrest/UserServiceImpl!com.monolitomicroservice.teste.session.service.UserService");
                            System.out.println("@@@@@@@@@ achou 5");
                            //System.out.println("########### " + service.getCurrentUser());
                            return service;
                        } catch (Exception ex5) {
                            System.out.println("EEEEEEEEE Erro 5: " + ex5.getMessage());
                            throw ex5;
                        }
                    }
                }
            }
        }
    }

    private static void traverseJndiNode(String nodeName, javax.naming.Context context) {
        try {
            NamingEnumeration<NameClassPair> list = context.list(nodeName);
            while (list.hasMore()) {
                NameClassPair ncp = list.next();
                String childName = nodeName + "/" + ncp.getName();
                System.out.println("$$$$$$$$$$$$$$ " + childName + " - " + ncp.getClassName());
                traverseJndiNode(childName, context);
            }
        } catch (NamingException ex) {
            // We reached a leaf
        }
    }

    private static void teste() {
        try {
            final Properties ejbProperties = new Properties();
            ejbProperties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
            ejbProperties.put(javax.naming.Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            ejbProperties.put("remote.connections", "1");
            ejbProperties.put("remote.connection.1.host", "sessionejbserver");
            ejbProperties.put("remote.connection.1.port", 8080);
            //ejbProperties.put("remote.connection.1.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER"); // needed for forcing authentication over remoting (i.e. if you have a custom login module)
            //ejbProperties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false"); // needed for a login module that requires the password in plaintext
            ejbProperties.put("remote.connection.1.username", "ejb");
            ejbProperties.put("remote.connection.1.password", "test");
            ejbProperties.put("org.jboss.ejb.client.scoped.context", "true"); // Not needed when EJBClientContext.setSelector is called programatically. ATTENTION: Client-Interceptor registration below does not work with this property! BUG?

            final EJBClientConfiguration ejbClientConfiguration = new PropertiesBasedEJBClientConfiguration(ejbProperties);
            final ConfigBasedEJBClientContextSelector selector = new ConfigBasedEJBClientContextSelector(ejbClientConfiguration);
            EJBClientContext.setSelector(selector);
            System.out.println(":::::::::::: TESTE OK");
        } catch (Exception ex) {
            System.out.println(":::::::::::: TESTE ERROR: " + ex.getMessage());
        }
    }
}
