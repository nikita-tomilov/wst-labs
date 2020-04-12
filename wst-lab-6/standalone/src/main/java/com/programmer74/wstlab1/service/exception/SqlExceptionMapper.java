package com.programmer74.wstlab1.service.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SqlExceptionMapper implements ExceptionMapper<SqlException> {
  @Override
  public Response toResponse(SqlException e) {
    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
  }
}