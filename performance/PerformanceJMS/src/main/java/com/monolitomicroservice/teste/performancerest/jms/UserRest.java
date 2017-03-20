package com.monolitomicroservice.teste.performancerest.jms;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.monolitomicroservice.teste.performance.common.RestResult;
import com.monolitomicroservice.teste.performance.common.UserVO;

@Path("/users")
public class UserRest {
    private static final boolean balanced;
    private static final Properties properties = new Properties();

    private static final Logger log = Logger.getLogger(UserRest.class.getName());

    private static Context context;
    private static ConnectionFactory factory;
    private static Destination queue;
    private static JMSContext jmsContext;

    static {
        balanced = System.getenv("BALANCED") != null && System.getenv("BALANCED").equals("true");
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        properties.put(Context.PROVIDER_URL, "http-remoting://" + (balanced ? "performancehalb:8081" : "performanceserver:8080"));
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

        Context context = getContext(cached.equals("true"));
        ConnectionFactory factory = (ConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
        Destination queue = (Destination) context.lookup("jms/queue/UserQueue");
        JMSContext jmxCtx = getJmsContext(cached.equals("true"), factory);

        try {
            JMSProducer producer = jmxCtx.createProducer();

            ObjectMessage message = jmxCtx.createObjectMessage();
            message.setStringProperty("action", "create");

            if (tenantId != null && tenantId > 0)
                message.setLongProperty("tenantId", tenantId.longValue());
            if (userCode != null)
                message.setStringProperty("userCode", userCode);
            if (login != null)
                message.setStringProperty("login", login);
            if (password != null)
                message.setStringProperty("password", password);
            if (email != null)
                message.setStringProperty("email", email);
            if (firstName != null)
                message.setStringProperty("firstName", firstName);
            if (lastName != null)
                message.setStringProperty("lastName", lastName);
            if (fullName != null)
                message.setStringProperty("fullName", fullName);
            if (birthDate != null && birthDate.longValue() > 0)
                message.setLongProperty("birthDate", birthDate.longValue());

            TemporaryQueue tempDest = jmxCtx.createTemporaryQueue();
            message.setJMSReplyTo(tempDest);
            message.setJMSCorrelationID(String.valueOf(System.nanoTime()));

            producer.send(queue, message);

            TextMessage msgRet = (TextMessage) jmxCtx.createConsumer(tempDest).receive(3000l);
            ObjectMapper mapper = new ObjectMapper();
            mapper.getSubtypeResolver().registerSubtypes(new NamedType(UserVO.class, "content"));
            RestResult cr = mapper.readValue(msgRet.getText(), RestResult.class);
            UserVO vo = mapper.convertValue(cr.getContent(), UserVO.class);

            RestResult r = new RestResult(System.currentTimeMillis() - ini, vo, System.getProperty("jboss.qualified.host.name"));
            r.setServerContainer(cr.getContainer());

            log.fine("==== User created: " + r);

            return r;
        } finally {
            if (jmxCtx != null && !cached.equals("true")) {
                jmxCtx.close();
            }
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/find")
    public RestResult find(@QueryParam("start") int start, @QueryParam("size") int size,
            @QueryParam("cached") @DefaultValue("false") String cached) throws Exception {
        long ini = System.currentTimeMillis();

        Context context = getContext(cached.equals("true"));
        ConnectionFactory factory = (ConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
        Destination queue = (Destination) context.lookup("jms/queue/UserQueue");
        JMSContext jmxCtx = getJmsContext(cached.equals("true"), factory);

        try {
            JMSProducer producer = jmxCtx.createProducer();

            ObjectMessage message = jmxCtx.createObjectMessage();
            message.setStringProperty("action", "find");
            if (start > 0)
                message.setIntProperty("start", start);
            if (size > 0)
                message.setIntProperty("size", size);

            TemporaryQueue tempDest = jmxCtx.createTemporaryQueue();
            message.setJMSReplyTo(tempDest);
            message.setJMSCorrelationID(String.valueOf(System.nanoTime()));

            producer.send(queue, message);

            TextMessage msgRet = (TextMessage) jmxCtx.createConsumer(tempDest).receive(3000l);

            ObjectMapper mapper = new ObjectMapper();
            RestResult cr = mapper.readValue(msgRet.getText(), RestResult.class);
            List<UserVO> l = Arrays.asList(mapper.convertValue(cr.getContent(), UserVO[].class));

            RestResult r = new RestResult(System.currentTimeMillis() - ini, l, System.getProperty("jboss.qualified.host.name"));
            r.setServerContainer(cr.getContainer());

            log.fine("==== Users found: " + r);

            return r;
        } finally {
            if (jmxCtx != null && !cached.equals("true")) {
                jmxCtx.close();
            }
        }
    }

    private static Context getContext(boolean cached) throws NamingException {
        if (cached && context != null) {
            return context;
        }
        Context ctx = new InitialContext(properties);
        if (cached) {
            context = ctx;
        }
        return ctx;
    }

    private static ConnectionFactory getFactory(Context context, boolean cached) throws NamingException {
        if (cached && factory != null) {
            return factory;
        }
        ConnectionFactory f = (ConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
        if (cached) {
            factory = f;
        }
        return f;
    }

    private static Destination getDestination(boolean cached) throws NamingException {
        if (cached && queue != null) {
            return queue;
        }
        Destination q = (Destination) context.lookup("jms/queue/UserQueue");
        if (cached) {
            queue = q;
        }
        return q;
    }

    private static JMSContext getJmsContext(boolean cached, ConnectionFactory factory) {
        if (cached && jmsContext != null) {
            return jmsContext;
        }
        JMSContext ctx = factory.createContext();
        if (cached) {
            jmsContext = ctx;
        }
        return ctx;
    }
}
