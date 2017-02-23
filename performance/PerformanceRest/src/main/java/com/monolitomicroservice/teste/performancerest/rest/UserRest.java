package com.monolitomicroservice.teste.performancerest.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/users")
public class UserRest {
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
        JSONObject json = responseToJSonObject(response);

        List<TSTUserVO> l = parseUsers(json.getJSONArray("content"));

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
        JSONObject json = responseToJSonObject(response);
        JSONObject content = json.getJSONObject("content");

        RestResult r;
        if (content != null) {
            TSTUserVO vo = parseUser(content);
            r = new RestResult(System.currentTimeMillis() - ini, vo);
        } else {
            r = new RestResult(System.currentTimeMillis() - ini, null);
        }

        return r;
    }

    private static JSONObject responseToJSonObject(HttpResponse response) throws IOException {
        StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = br.readLine();
        while (line != null) {
            writer.write(line);
            line = br.readLine();
        }

        JSONObject json = new JSONObject(writer.toString());
        return json;
    }

    private static List<TSTUserVO> parseUsers(JSONArray content) {
        List<TSTUserVO> r = new ArrayList<>();
        if (content != null) {
            for (int i = 0; i < content.length(); i++) {
                r.add(parseUser(content.getJSONObject(i)));
            }
        }
        return r;
    }

    private static TSTUserVO parseUser(JSONObject content) {
        TSTUserVO vo = new TSTUserVO();
        for (Object key : content.keySet()) {
            switch (key.toString()) {
                case "id":
                    vo.setId(content.getLong("id"));
                    break;
                case "tenantId":
                    vo.setTenantId(content.getLong("tenantId"));
                    break;
                case "userCode":
                    vo.setUserCode(content.getString("userCode"));
                    break;
                case "login":
                    vo.setLogin(content.getString("login"));
                    break;
                case "password":
                    vo.setPassword(content.getString("password"));
                    break;
                case "email":
                    vo.setEmail(content.getString("email"));
                    break;
                case "firstName":
                    vo.setFirstName(content.getString("firstName"));
                    break;
                case "lastName":
                    vo.setLastName(content.getString("lastName"));
                    break;
                case "fullName":
                    vo.setFullName(content.getString("fullName"));
                    break;
                case "birthDate":
                    vo.setBirthDate(new Date(content.getLong("birthDate")));
                    break;
            }
        }
        return vo;
    }

    private static String getServerURL() {
        return "http://172.18.0.6:8080/teste";
    }
}
