package com.programmer74.wstlab1.util;

public class ServerBadRequestException extends Exception {
  public ServerBadRequestException(int status, String message) {
    super(status + " " + message);
  }
}