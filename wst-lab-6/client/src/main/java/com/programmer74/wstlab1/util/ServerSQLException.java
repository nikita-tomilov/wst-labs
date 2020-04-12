package com.programmer74.wstlab1.util;

public class ServerSQLException extends Exception {
  public ServerSQLException(int status, String message) {
    super(status + " " + message);
  }
}