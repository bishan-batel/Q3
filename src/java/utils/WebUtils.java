package utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public final class WebUtils {
  public static String randomUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static String webInf(String path) {
    return String.format("WEB-INF/%s", path);
  }

  public static void setError(HttpServletRequest req, String err) {
    req.getServletContext().setAttribute("error", err);
  }

  public static void redirect(HttpServletRequest req, HttpServletResponse res, String to) throws ServletException, IOException {
    req.getRequestDispatcher(to).forward(req, res);
  }

  public static void pageRedirect(HttpServletRequest req, HttpServletResponse res, String to)
          throws ServletException, IOException {
    req.getServletContext().setAttribute("redirectTo", to);
    req.getRequestDispatcher(webInf("redirecter.jsp")).forward(req, res);
  }

  private WebUtils() {
  }
}
