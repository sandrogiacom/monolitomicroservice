package com.monolitomicroservice.teste.performancerest.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.monolitomicroservice.teste.performance.common.RestResult;
import com.monolitomicroservice.teste.performance.common.UserVO;

@Path("/users")
public class UserRest {
    private static final Logger log = Logger.getLogger(UserRest.class.getName());

    private static final boolean balanced = System.getenv("BALANCED") != null && System.getenv("BALANCED").equals("true");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/find")
    public RestResult find(@QueryParam("start") int start, @QueryParam("size") int size) throws Exception {
        long ini = System.currentTimeMillis();
        if (start < 0)
            start = 0;
        if (size <= 0)
            size = 50;

        String url = getServerURL() + "/users/find?start=" + start + "&size=" + size;
        HttpGet get = new HttpGet(url);

        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(get);
        RestResult cr = responseToJSonObject(response);

        RestResult r = new RestResult(System.currentTimeMillis() - ini,
                cr.getContent(), System.getProperty("jboss.qualified.host.name"));

        r.setServerContainer(cr.getContainer());
        log.fine("==== Users found: " + ((List) r.getContent()).size());

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
        String url = getServerURL() + "/users/create";

        HttpPost post = new HttpPost(url);
        List<NameValuePair> pars = new ArrayList<>();
        if (userCode != null) {
            pars.add(new BasicNameValuePair("userCode", userCode));
        }

        if (tenantId != null) {
            pars.add(new BasicNameValuePair("tenantId", String.valueOf(tenantId)));
        }
        if (login != null) {
            pars.add(new BasicNameValuePair("login", login));
        }
        if (password != null) {
            pars.add(new BasicNameValuePair("password", password));
        }
        if (email != null) {
            pars.add(new BasicNameValuePair("email", email));
        }
        if (firstName != null) {
            pars.add(new BasicNameValuePair("firstName", firstName));
        }
        if (lastName != null) {
            pars.add(new BasicNameValuePair("lastName", lastName));
        }
        if (fullName != null) {
            pars.add(new BasicNameValuePair("fullName", fullName));
        }
        if (birthDate != null) {
            pars.add(new BasicNameValuePair("birthDate", String.valueOf(birthDate)));
        }

        post.setEntity(new UrlEncodedFormEntity(pars, "UTF-8"));
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(post);
        RestResult cr = responseToJSonObject(response);

        RestResult r = new RestResult(System.currentTimeMillis() - ini,
                cr.getContent(), System.getProperty("jboss.qualified.host.name"));

        r.setServerContainer(cr.getContainer());
        log.fine("==== User created: " + r);

        return r;
    }

    private static RestResult responseToJSonObject(HttpResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSubtypeResolver().registerSubtypes(new NamedType(UserVO.class, "content"));
        RestResult cr = mapper.readValue(response.getEntity().getContent(), RestResult.class);
        if (cr.getContent() != null) {
            if (cr.getContent() instanceof List) {
                cr.setContent(Arrays.asList(mapper.convertValue(cr.getContent(), UserVO[].class)));
            } else if (cr.getContent() instanceof Map) {
                cr.setContent(mapper.convertValue(cr.getContent(), UserVO.class));
            }
        }
        return cr;
    }


    private static String getServerURL() {
        return balanced ? "http://performancehalb:8081/teste" : "http://performanceha:8080/teste";
    }
}
