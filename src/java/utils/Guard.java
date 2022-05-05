package utils;

import java.util.Arrays;

public final class Guard {
  public static void clamp(Object map, Object... matches) {
    for (Object obj : matches) {
      if (obj.equals(map))
        return;
    }
    throw new IllegalArgumentException(map.toString());
  }

  public static void againstNull(Object... objs) {
    for (Object o : objs) {
      if (o == null) throw new NullPointerException();
    }
  }

  public static void againstNullOrWhitespace(String... objs) {
    for (String o : objs) {
      if (o == null || o.isEmpty()) throw new NullPointerException();
    }
  }

  private Guard() {
  }
}
