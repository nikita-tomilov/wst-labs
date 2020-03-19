package com.programmer74.wstlab1.util;

import lombok.Getter;
import lombok.Setter;

public enum Command {
  FIND_ALL("Вывести всех пользователей"),
  FIND_BY_FILTERS("Применить фильтры"),
  QUIT("Выйти");

  @Getter
  @Setter
  private String help;

  Command(String help) {
    this.help = help;
  }
}
