package com.programmer74.wstlab1.service;

import com.programmer74.wstlab1.api.UserCreateDTO;
import com.programmer74.wstlab1.api.UserDTO;
import com.programmer74.wstlab1.api.UsersDTO;
import com.programmer74.wstlab1.api.UsersServiceApi;
import com.programmer74.wstlab1.database.dao.UserDAO;
import com.programmer74.wstlab1.database.entity.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

@Data
@Slf4j
@Path("/users")
@ManagedBean
public class UsersService implements UsersServiceApi {

  @Resource(mappedName = "jdbc/users")
  private DataSource dataSource;

  private UserDAO userDAO;

  @PostConstruct
  public void init() {
    userDAO = new UserDAO(dataSource);
  }

  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  @GET
  @Path("/all")
  @Produces({MediaType.APPLICATION_JSON})
  public UsersDTO findAll() throws SQLException {
    return new UsersDTO(userDAO.findAll().stream().map(this::fromEntity)
        .collect(Collectors.toList()));
  }

  @Override
  @GET
  @Path("/filter")
  @Produces({MediaType.APPLICATION_JSON})
  public UsersDTO findWithFilters(
      @QueryParam("id") Long id, @QueryParam("login") String login,
      @QueryParam("password") String password, @QueryParam("email") String email,
      @QueryParam("gender") Boolean gender, @QueryParam("registerDate") String registerDate
  )
  throws SQLException {
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
    return new UsersDTO(userDAO.findWithFilters(id, login, password, email, gender, date).stream()
        .map(this::fromEntity)
        .collect(Collectors.toList()));
  }

  @Override
  @DELETE
  @Path("/{id}")
  @WebMethod
  @Produces(MediaType.TEXT_PLAIN)
  public String delete(@PathParam("id") Long id) {
    try {
      if (id == null) {
        return "Id can't be null";
      }
      int delete = userDAO.delete(id);
      if (delete <= 0) {
        return String.format("Can't delete User. User with specified id: %s not found ", id);
      }
      return String.valueOf(delete);
    } catch (SQLException e) {
      return "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
    }
  }

  @Override
  @POST
  @WebMethod
  @Produces(MediaType.TEXT_PLAIN)
  public String insert(UserCreateDTO userCreateDTO) {
    try {
      Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(userCreateDTO.getRegisterDate());
      return String.valueOf(userDAO.insert(userCreateDTO.getLogin(), userCreateDTO.getPassword(),
          userCreateDTO.getEmail(), userCreateDTO.getGender(), parse)
      );
    } catch (SQLException e) {
      String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return String.valueOf(-1L);
  }

  @Override
  @PUT
  @Path("/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public String update(@PathParam("id") Long id, UserDTO userDTO) {
    int update = 0;
    try {
      Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(userDTO.getRegisterDate());
      update = userDAO.update(id, userDTO.getLogin(), userDTO.getPassword(),
          userDTO.getEmail(), userDTO.getGender(), parse);
      if (update <= 0) {
        return String.format("Can't update User. User with specified id: %s not found ", id);
      }
    } catch (SQLException e) {
      return "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return String.valueOf(update);
  }

  private UserDTO fromEntity(User u) {
    return new UserDTO(
        u.getId(),
        u.getLogin(),
        u.getPassword(),
        u.getEmail(),
        u.getGender(),
        sdf.format(u.getRegisterDate()));
  }
}
