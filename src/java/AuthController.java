
import auth.Auth;
import auth.DuplicateEmailException;
import auth.InvalidPasswordException;
import auth.session.Session;
import utils.Email;
import utils.Guard;
import utils.WebUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

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
	public static final int SESSION_COOKIE_EXPIRE = 30 * 23 * 60 * 60;

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
		applyToken(res, null);

		HttpSession session = req.getSession();
		if (session != null) {
			Enumeration<String> names = session.getAttributeNames();
			while (names.hasMoreElements()) session.removeAttribute(names.nextElement());
		}
		WebUtils.pageRedirect(req, res, "account");
	}

	private void handleLogin(HttpServletRequest req, HttpServletResponse res)
					throws ServletException, IOException {
		try {
			String email = req.getParameter("email");
			String password = req.getParameter("password");

			// guards
			Guard.forNullOrWhitespace(email, password);

			Session session = Auth.login(email, password);

			applySession(res, session);
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
		try {
			String email = req.getParameter("email");
			String password = req.getParameter("password");
			String firstName = req.getParameter("firstName");
			String lastName = req.getParameter("lastName");
			String accountType = req.getParameter("accountType");

			Guard.forNullOrWhitespace(email, password, firstName, lastName, accountType);
			Guard.whitelist(accountType, "student", "teacher");
			if (!Email.isValid(email)) throw new IllegalArgumentException();

			Session session = Auth.register(
							email,
							password,
							firstName,
							lastName,
							Objects.equals(accountType, "student") ? Auth.AccountType.STUDENT : Auth.AccountType.TEACHER
			);

			applySession(res, session);
			WebUtils.pageRedirect(req, res, "home");
			return;
		} catch (DuplicateEmailException e) {
			WebUtils.setError(req, "Email already has an account attached");
		} catch (NullPointerException | IllegalArgumentException e) {
			WebUtils.setError(req, "Invalid form data");
			e.printStackTrace();
		} catch (Exception e) {
			WebUtils.setError(req, "Internal Server");
		}

		WebUtils.pageRedirect(req, res, "account");
	}

	public static void applySession(HttpServletResponse res, Session session) {
		applyToken(res, session.getToken());
	}

	public static void applyToken(HttpServletResponse res, String tok) {
		Cookie cookie = new Cookie("token", tok);
		cookie.setMaxAge(SESSION_COOKIE_EXPIRE);
		cookie.setSecure(true);
		res.addCookie(cookie);
	}

	public static Optional<String> getToken(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		if (cookies == null || cookies.length == 0) return Optional.empty();
		return Arrays.stream(req.getCookies())
						.filter(cookie -> cookie.getName().equals("token"))
						.findFirst()
						.map(Cookie::getValue);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Optional<String> token = getToken(req);

		try {
			if (token.isPresent() && Session.isValidSession(token.get())) {
				WebUtils.pageRedirect(req, res, "home");
				return;
			}
		} catch (SQLException e) {
			// ignored
		}
		WebUtils.redirect(req, res, WebUtils.webInf("account.jsp"));
	}
}
