package com.monolitomicroservice.teste.session.service;

import java.util.Properties;

import javax.naming.InitialContext;

public class TesteConexao {
    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
            properties.put(javax.naming.Context.PROVIDER_URL, "http-remoting://172.19.0.5:8080");
            //properties.put(javax.naming.Context.SECURITY_PRINCIPAL, "ejb");
            //properties.put(javax.naming.Context.SECURITY_CREDENTIALS, "test");
            properties.put(javax.naming.Context.SECURITY_PRINCIPAL, "user1");
            properties.put(javax.naming.Context.SECURITY_CREDENTIALS, "user1");
            properties.put("jboss.naming.client.ejb.context", true);
            properties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");

            javax.naming.Context context = new InitialContext(properties);

            UserService service = (UserService) context.lookup("sessionserverejb/sessionrest/UserServiceImpl!com.monolitomicroservice.teste.session.service.UserService");

            String s = service.getCurrentUser();
            System.out.println("Result: " + s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
