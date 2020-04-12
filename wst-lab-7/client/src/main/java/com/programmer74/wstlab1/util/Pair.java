package com.programmer74.wstlab1.util;

import lombok.Value;

@Value
public class Pair<L, R> {
  private final L left;

  private final R right;
}