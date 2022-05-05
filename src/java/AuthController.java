import auth.Auth;
import auth.DuplicateEmailException;
import auth.InvalidPasswordException;
import auth.session.Session;
import utils.Guard;
import utils.WebUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bishan
 */
@WebServlet(name = "AuthController", loadOnStartup = 1, urlPatterns = {
        "/account",
        "/register",
        "/login",
        "/logout"
})
public class AuthController extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    switch (req.getServletPath()) {
      case "/register":
        handleRegister(req, res);
        break;
      case "/login":
        handleLogin(req, res);
        break;
      case "/logout":
        handleLogout(req, res);
      default:
        WebUtils.pageRedirect(req, res, "account");
    }
  }

  private void handleLogout(HttpServletRequest req, HttpServletResponse res)
          throws ServletException, IOException {
    req.getSession().setAttribute("token", null);
    WebUtils.pageRedirect(req, res, "account");
  }


  private void handleLogin(HttpServletRequest req, HttpServletResponse res)
          throws ServletException, IOException {
    String err;
    try {
      String email = req.getParameter("email");
      String password = req.getParameter("password");

      // guards
      Guard.againstNullOrWhitespace(email, password);

      Session session = Auth.login(email, password);
      req.getSession(true).setAttribute("token", session.getToken());
      WebUtils.pageRedirect(req, res, "home");
      return;
    } catch (InvalidPasswordException e) {
      WebUtils.setError(req, "Invalid Password");
    } catch (AccountNotFoundException e) {
      WebUtils.setError(req, "Account not found");
    } catch (NullPointerException e) {
      WebUtils.setError(req, "Invalid form data");
    } catch (Exception e) {
      WebUtils.setError(req, "Internal Server Error");
    }
    WebUtils.pageRedirect(req, res, "account");
  }

  private void handleRegister(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String err;

    try {
      String email = req.getParameter("email");
      String password = req.getParameter("password");
      String firstName = req.getParameter("firstName");
      String lastName = req.getParameter("lastName");
      String accountType = req.getParameter("accountType");

      Guard.againstNullOrWhitespace(email, password, firstName, lastName, accountType);
      Guard.clamp(accountType, "student", "teacher");

      Session session = Auth.register(
              email,
              password, 
              firstName,
              lastName,
              Objects.equals(accountType, "student") ? Auth.AccountType.STUDENT :
                      Auth.AccountType.TEACHER
      );

      req.getSession(true).setAttribute("token", session.getToken());
      WebUtils.pageRedirect(req, res, "home");
      return;
    } catch (DuplicateEmailException e) {
      err = "Email already has an account attached";
    } catch (NullPointerException | IllegalArgumentException e) {
      err = "Invalid form data";
    } catch (Exception e) {
      err = "Internal Server";
    }

    req.getServletContext().setAttribute("error", err);
    WebUtils.pageRedirect(req, res, "account");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String token = (String) req.getSession().getAttribute("token");

    try {
      if (token != null && Session.isValidSession(token)) {
        WebUtils.pageRedirect(req, res, "home");
        return;
      }
    } catch (SQLException e) {
      // ignored
    }
    WebUtils.redirect(req, res, WebUtils.webInf("account.jsp"));
  }
}
