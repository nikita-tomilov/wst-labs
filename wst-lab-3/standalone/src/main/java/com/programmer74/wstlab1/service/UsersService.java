package com.programmer74.wstlab1.service;

import com.programmer74.wstlab1.database.UserDAO;
import com.programmer74.wstlab1.database.entity.User;
import com.programmer74.wstlab1.exceptions.UserServiceException;
import com.programmer74.wstlab1.exceptions.UserServiceFault;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@WebService(serviceName = "users", targetNamespace = "users_namespace")
@AllArgsConstructor
@NoArgsConstructor
public class UsersService {
  private UserDAO userDAO;

  @Resource
  private WebServiceContext wsctx;

  public UsersService(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @WebMethod
  public List<User> findAll() throws SQLException {
    return userDAO.findAll();
  }

  @WebMethod
  public List<User> findWithFilters(
      @WebParam(name = "id") Long id,
      @WebParam(name = "login") String login,
      @WebParam(name = "password") String password,
      @WebParam(name = "email") String email,
      @WebParam(name = "gender") Boolean gender,
      @WebParam(name = "registerDate") XMLGregorianCalendar registerDate
  ) throws SQLException {
    return userDAO.findWithFilters(id, login, password, email, gender, registerDate);
  }

  @WebMethod
  public int delete(@WebParam(name = "id") Long id) throws UserServiceException {
    checkCredentials();
    try {
      if (id == null) {
        String message = "Id can't be null";
        throw new UserServiceException(message, new UserServiceFault(message));
      }
      int delete = userDAO.delete(id);
      if (delete <= 0) {
        String message = String
            .format("Can't delete User. User with specified id: %s not found ", id);
        throw new UserServiceException(message, new UserServiceFault(message));
      }
      return delete;
    } catch (SQLException e) {
      String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
      throw new UserServiceException(message, e, new UserServiceFault(message));
    }
  }

  @WebMethod
  public Long insert(
      @WebParam(name = "login") String login, @WebParam(name = "password") String password,
      @WebParam(name = "email") String email, @WebParam(name = "gender") Boolean gender,
      @WebParam(name = "registerDate") XMLGregorianCalendar registerDate
  ) throws UserServiceException {
    checkCredentials();
    try {
      return userDAO.insert(login, password, email, gender, registerDate);
    } catch (SQLException e) {
      String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
      throw new UserServiceException(message, e, new UserServiceFault(message));
    }
  }

  @WebMethod
  public int update(
      @WebParam(name = "id") Long id, @WebParam(name = "login") String login,
      @WebParam(name = "password") String password, @WebParam(name = "email") String email,
      @WebParam(name = "gender") Boolean gender,
      @WebParam(name = "registerDate") XMLGregorianCalendar registerDate
  ) throws UserServiceException {
    checkCredentials();
    int update = 0;
    try {
      update = userDAO.update(id, login, password, email, gender, registerDate);
      if (update <= 0) {
        String message = String
            .format("Can't update User. User with specified id: %s not found ", id);
        throw new UserServiceException(message, new UserServiceFault(message));
      }
    } catch (SQLException e) {
      String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
      throw new UserServiceException(message, e, new UserServiceFault(message));
    }
    return update;
  }

  public void checkCredentials() throws UserServiceException {
    MessageContext mctx = wsctx.getMessageContext();

    Map http_headers = (Map) mctx.get(MessageContext.HTTP_REQUEST_HEADERS);
    List basicAuthList = (List) http_headers.get("Authorization");

    if ((basicAuthList == null) || (basicAuthList.isEmpty())) {
      String message = "Auth required!";
      throw new UserServiceException(message, new UserServiceFault(message));
    }

    String header = basicAuthList.get(0).toString();

    String authType = header.split(" ")[0];
    String authParam = header.split(" ")[1];

    if (!authType.equals("Basic")) {
      String message = "Unknown auth type " + authType;
      throw new UserServiceException(message, new UserServiceFault(message));
    }

    String decodedAuth = new String(Base64.getDecoder().decode(authParam));
    String username = decodedAuth.split(":")[0];
    String password = decodedAuth.split(":")[1];

    List<User> users;
    try {
      users = userDAO.findWithFilters(null, username, null, null, null, null);
    } catch (SQLException e) {
      String message = e.getMessage();
      throw new UserServiceException(message, new UserServiceFault(message));
    }
    if (users.isEmpty()) {
      String message = "Unknown user!";
      throw new UserServiceException(message, new UserServiceFault(message));
    }
    User user = users.get(0);
    if (!(user.getPassword().equals(password))) {
      String message = "Wrong password!";
      throw new UserServiceException(message, new UserServiceFault(message));
    }

    System.out.println("AUTH USING " + username + " " + password);
  }
}
