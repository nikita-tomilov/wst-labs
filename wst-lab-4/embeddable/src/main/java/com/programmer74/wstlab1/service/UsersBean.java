package com.programmer74.wstlab1.service;

import com.programmer74.wstlab1.database.dao.UserDAO;
import lombok.Data;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

@Data
@ManagedBean
public class UsersBean {
  @Resource(mappedName = "jdbc/users")
  private DataSource dataSource;

  @Produces
  public UserDAO userDAO() {
    return new UserDAO(dataSource);
  }
}
