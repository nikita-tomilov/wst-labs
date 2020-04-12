package com.programmer74.wstlab1.util;

import lombok.Getter;
import lombok.Setter;

public enum JUDDICommand {
  LIST_BUSINESS("Список бизнесов"),
  CREATE_BUSINESS("Зарегистрировать бизнес"),
  CREATE_SERVICE("Зарегистрировать сервис"),
  FIND_SERVICE("Найти сервис"),
  USE_SERVICE("Использовать сервис"),
  QUIT("Выйти");

  @Getter
  @Setter
  private String help;

  JUDDICommand(String help) {
    this.help = help;
  }
}
