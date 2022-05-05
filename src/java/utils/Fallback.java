package utils;

import java.util.Scanner;

public final class Fallback {
  public static <T> T againstNull(T test, T backup) {
    return test == null ? backup : test;
  }

  public static String againstNullOrWhitespace(String test, String backup) {
    return test == null || test.isEmpty() ? backup : test;
  }

  private Fallback() {
  }

  public static void main(String[] args) {
  }
}
