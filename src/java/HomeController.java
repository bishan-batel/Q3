import auth.account.Account;
import auth.account.Classroom;
import auth.account.Student;
import auth.account.Teacher;
import auth.session.Session;
import utils.Guard;
import utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @author bishan
 */
@WebServlet(name = "HomeController", loadOnStartup = 1, urlPatterns = {
				"/home",
				"/home-create-classroom"
})
public class HomeController extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();

		try {
			Optional<Account> possibleAccount =
							Session.getAccountFromToken((String) req.getSession().getAttribute("token"));

			// if has an account
			if (possibleAccount.isPresent()) {
				Account account = possibleAccount.get();

				if (Student.isStudent(account)) {
					handleStudentPost(req, res, Student.getFromAccount(account));
				} else {
					handleTeacherPost(req, res, Teacher.getFromAccount(account));
				}

				return;
			}
			WebUtils.pageRedirect(req, res, "account");

		} catch (SQLException e) {
			WebUtils.pageRedirect(req, res, "home");
		}

	}

	private void handleStudentPost(HttpServletRequest req, HttpServletResponse res, Student account) {
	}

	private void handleTeacherPost(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws ServletException, IOException, SQLException {
		switch (req.getServletPath()) {
			case "/home-create-classroom":
				createClassroom(req, res, teacher);
				return;

			default:
				WebUtils.pageRedirect(req, res, "home");
		}
	}

	private void createClassroom(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws SQLException, ServletException, IOException {

		try {
			String name = req.getParameter("classroomName");
			Guard.againstNullOrWhitespace(name);

			Classroom.createFor(teacher, name);
		} catch (NullPointerException e) {
			// ignored
		}
		WebUtils.pageRedirect(req, res, "home");
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			// checks if logged in
			String token = (String) req.getSession().getAttribute("token");
			Optional<Account> possibleAccount = Session.getAccountFromToken(token);

			if (possibleAccount.isPresent()) {
				Account acc = possibleAccount.get();
				req.setAttribute("account", acc);

				if (Student.isStudent(acc))
					handleStudent(req, res, acc);
				else
					handleTeacher(req, res);

				return;
			}

			WebUtils.setError(req, "You must be logged in to access a home page");
		} catch (SQLException e) {
			WebUtils.setError(req, "Internal Server Error");
		} catch (Exception e) {
			WebUtils.setError(req, "Unknown Error");
		}
		WebUtils.pageRedirect(req, res, "account");
	}

	private void handleTeacher(HttpServletRequest req, HttpServletResponse res)
					throws ServletException, IOException {
		WebUtils.redirect(req, res, WebUtils.webInf("home.teacher.jsp"));
	}

	private void handleStudent(HttpServletRequest req, HttpServletResponse res, Account account)
					throws ServletException, IOException, SQLException {
		req.setAttribute("student", Student.getFromAccount(account));
		WebUtils.redirect(req, res, WebUtils.webInf("home.student.jsp"));
	}
}
