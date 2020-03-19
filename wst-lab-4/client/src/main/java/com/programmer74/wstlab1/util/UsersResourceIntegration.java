package com.programmer74.wstlab1.util;

import com.programmer74.wstlab1.client.service.User;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.slf4j.Slf4j;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class UsersResourceIntegration {
  private final static String BASE_URL = "http://localhost:8080/users/";

  private final static String FIND_ALL_URL = BASE_URL + "all";

  private final static String FILTER_URL = BASE_URL + "filter";

  private final static String UPDATE_URL = BASE_URL + "%d";

  private final static String DELETE_URL = BASE_URL + "%d";

  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  public List<User> findAll() {
    Client client = Client.create();
    WebResource webResource = client.resource(FIND_ALL_URL);
    return getUsers(webResource);
  }

  public List<User> findWithFilters(
      Long id,
      String login,
      String password,
      String email,
      Boolean gender,
      String registerDate
  ) {
    Client client = Client.create();
    WebResource webResource = client.resource(FILTER_URL);
    if (id != null) {
      webResource = webResource.queryParam("id", id + "");
    }
    if (login != null) {
      webResource = webResource.queryParam("login", login);
    }
    if (password != null) {
      webResource = webResource.queryParam("password", password);
    }
    if (email != null) {
      webResource = webResource.queryParam("email", email);
    }
    if (gender != null) {
      webResource = webResource.queryParam("gender", gender.toString());
    }
    if (registerDate != null) {
      webResource = webResource.queryParam("registerDate", registerDate);
    }
    return getUsers(webResource);
  }

  private List<User> getUsers(final WebResource webResource) {
    ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
        .get(ClientResponse.class);
    if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
      throw new IllegalStateException("Request failed. HTTP code: " + response.getStatus());
    }
    GenericType<List<User>> type = new GenericType<List<User>>() {
    };
    return response.getEntity(type);
  }
}