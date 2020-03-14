package com.programmer74.wstlab1.service;

import com.programmer74.wstlab1.database.UserDAO;
import lombok.Data;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

@Data
@ApplicationScoped
public class UsersBean {
  @Resource(lookup = "jdbc/users")
  private DataSource dataSource;

  @Produces
  public UserDAO userDAO() {
    return new UserDAO(dataSource);
  }
}
