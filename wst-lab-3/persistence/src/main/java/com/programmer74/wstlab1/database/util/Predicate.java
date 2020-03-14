package com.programmer74.wstlab1.database.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Predicate {
  StringBuilder builder;

  public Predicate and(String where) {
    if (builder == null) {
      builder = new StringBuilder(where);
    } else {
      builder.append(" AND ").append(where);
    }
    return this;
  }

  public Predicate comma(String value) {
    if (builder == null) {
      builder = new StringBuilder("'" + value + "'");
    } else {
      builder.append(",'").append(value).append("'");
    }
    return this;
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}
