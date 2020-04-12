package com.programmer74.wstlab1.service;

import com.programmer74.wstlab1.database.dao.UserDAO;
import com.programmer74.wstlab1.database.dto.UserDTO;
import com.programmer74.wstlab1.database.entity.User;
import com.programmer74.wstlab1.service.exception.SqlException;
import com.programmer74.wstlab1.service.exception.UserServiceException;
import com.programmer74.wstlab1.standalone.App;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import javax.jws.WebMethod;
import javax.sql.DataSource;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Data
@Slf4j
@Path("/users")
public class UsersService {
  private UserDAO userDAO;

  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  public UsersService() {
    InputStream dsPropsStream = App.class.getClassLoader()
        .getResourceAsStream("application.properties");
    Properties dsProps = new Properties();
    try {
      dsProps.load(dsPropsStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    HikariConfig hikariConfig = new HikariConfig(dsProps);
    DataSource ds = new HikariDataSource(hikariConfig);
    userDAO = new UserDAO(ds);
  }

  @GET
  @Path("/all")
  @Produces({MediaType.APPLICATION_JSON})
  public List<User> findAll() throws SQLException {
    return userDAO.findAll();
  }

  @GET
  @Path("/filter")
  @Produces({MediaType.APPLICATION_JSON})
  public List<User> findWithFilters(
      @QueryParam("id") Long id, @QueryParam("login") String login,
      @QueryParam("password") String password, @QueryParam("email") String email,
      @QueryParam("gender") Boolean gender, @QueryParam("registerDate") String registerDate
  ) throws SqlException {
    Date date;
    if (registerDate != null) {
      try {
        date = sdf.parse(registerDate);
      } catch (ParseException e) {
        date = null;
      }
    } else {
      date = null;
    }
    try {
      return userDAO.findWithFilters(id, login, password, email, gender, date);
    } catch (SQLException ex) {
      throw new SqlException(ex.getMessage());
    }
  }

  @DELETE
  @Path("/{id}")
  @WebMethod
  @Produces(MediaType.TEXT_PLAIN)
  public String delete(@PathParam("id") Long id) throws UserServiceException, SqlException {
    try {
      if (id == null) {
        throw new UserServiceException("Id can't be null");
      }
      int delete = userDAO.delete(id);
      if (delete <= 0) {
        throw new UserServiceException(String
            .format("Can't delete User. User with specified id: %s not found ", id));
      }
      return String.valueOf(delete);
    } catch (SQLException e) {
      throw new SqlException("SQL exception: " + e.getMessage() + ". State: " + e.getSQLState());
    }
  }

  @POST
  @WebMethod
  @Produces(MediaType.TEXT_PLAIN)
  public String insert(UserDTO userDTO) throws UserServiceException, SqlException {
    try {
      Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(userDTO.getRegisterDate());
      return String.valueOf(userDAO.insert(userDTO.getLogin(), userDTO.getPassword(),
          userDTO.getEmail(), userDTO.getGender(), parse)
      );
    } catch (SQLException e) {
      throw new SqlException("SQL exception: " + e.getMessage() + ". State: " + e.getSQLState());
    } catch (ParseException e) {
      e.printStackTrace();
      throw new UserServiceException("Cannot parse date " + userDTO.getRegisterDate());
    }
  }

  @PUT
  @Path("/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public String update(@PathParam("id") Long id, UserDTO userDTO)
  throws UserServiceException, SqlException {
    int update = 0;
    try {
      Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(userDTO.getRegisterDate());
      update = userDAO.update(id, userDTO.getLogin(), userDTO.getPassword(),
          userDTO.getEmail(), userDTO.getGender(), parse);
      if (update <= 0) {
        throw new UserServiceException(String
            .format("Can't update User. User with specified id: %s not found ", id));
      }
      return String.valueOf(update);
    } catch (SQLException e) {
      throw new SqlException("SQL exception: " + e.getMessage() + ". State: " + e.getSQLState());
    } catch (ParseException e) {
      e.printStackTrace();
      throw new UserServiceException("Cannot parse date " + userDTO.getRegisterDate());
    }
  }
}
