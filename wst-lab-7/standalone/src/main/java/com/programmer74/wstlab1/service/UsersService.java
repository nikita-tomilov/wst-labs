package com.programmer74.wstlab1.service;

import com.programmer74.wstlab1.database.UserDAO;
import com.programmer74.wstlab1.database.entity.User;
import com.programmer74.wstlab1.exceptions.UserServiceException;
import com.programmer74.wstlab1.exceptions.UserServiceFault;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@WebService(serviceName = "users", targetNamespace = "users_namespace")
@AllArgsConstructor
@NoArgsConstructor
public class UsersService {
  private UserDAO userDAO;

  @WebMethod
  public List<User> findAll() throws SQLException {
    return userDAO.findAll();
  }

  @WebMethod
  public List<User> findWithFilters(@WebParam(name = "id") Long id, @WebParam(name = "login") String login,
      @WebParam(name = "password") String password, @WebParam(name = "email") String email,
      @WebParam(name = "gender") Boolean gender, @WebParam(name = "registerDate") XMLGregorianCalendar registerDate) throws SQLException {
    return userDAO.findWithFilters(id, login, password, email, gender, registerDate);
  }

  @WebMethod
  public int delete(@WebParam(name = "id") Long id) throws UserServiceException {
    try {
      if (id == null ) {
        String message = "Id can't be null";
        throw new UserServiceException(message, new UserServiceFault(message));
      }
      int delete = userDAO.delete(id);
      if (delete <= 0) {
        String message = String.format("Can't delete User. User with specified id: %s not found ", id);
        throw new UserServiceException(message, new UserServiceFault(message));
      }
      return delete;
    } catch (SQLException e) {
      String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
      throw new UserServiceException(message, e, new UserServiceFault(message));
    }
  }

  @WebMethod
  public Long insert(@WebParam(name = "login") String login, @WebParam(name = "password") String password,
      @WebParam(name = "email") String email, @WebParam(name = "gender") Boolean gender,
      @WebParam(name = "registerDate") XMLGregorianCalendar registerDate) throws UserServiceException {
    try {
      return userDAO.insert(login, password, email, gender, registerDate);
    } catch (SQLException e) {
      String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
      throw new UserServiceException(message, e, new UserServiceFault(message));
    }
  }

  @WebMethod
  public int update(@WebParam(name = "id") Long id, @WebParam(name = "login") String login,
      @WebParam(name = "password") String password, @WebParam(name = "email") String email,
      @WebParam(name = "gender") Boolean gender,
      @WebParam(name = "registerDate") XMLGregorianCalendar registerDate) throws UserServiceException {
    int update = 0;
    try {
      update = userDAO.update(id, login, password, email, gender, registerDate);
      if (update <= 0) {
        String message = String.format("Can't update User. User with specified id: %s not found ", id);
        throw new UserServiceException(message, new UserServiceFault(message));
      }
    } catch (SQLException e) {
      String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
      throw new UserServiceException(message, e, new UserServiceFault(message));
    }
    return update;
  }
}
