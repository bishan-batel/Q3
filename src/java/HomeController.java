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
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @author bishan
 */
@WebServlet(name = "HomeController", loadOnStartup = 1, urlPatterns = {"/home", "/home-create-classroom", "/home-delete-classroom", "/classroom"})
public class HomeController extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();

		try {
			Optional<Account> possibleAccount =
							Session.getAccountFromToken(AuthController.getToken(req).orElse(""));

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

			case "/home-delete-classroom":
				deleteClassroom(req, res, teacher);
				return;

			default:
				WebUtils.pageRedirect(req, res, "home");
		}
	}

	private void deleteClassroom(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws SQLException, ServletException, IOException {
		try {
			String classroomId = req.getParameter("classroomId");
			Guard.againstNullOrWhitespace(classroomId);

			if (teacher.ownsClassroom(classroomId)) {
				Classroom.deleteById(classroomId);
			} else {
				WebUtils.setError(req, "Invalid classroom id");
			}
		} catch (NullPointerException e) {
			WebUtils.setError(req, "Invalid classroom id");
		}
		WebUtils.pageRedirect(req, res, "home");
	}

	private void createClassroom(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws SQLException, ServletException, IOException {

		try {
			String name = req.getParameter("classroomName");
			Guard.againstNullOrWhitespace(name);

			Classroom.createFor(teacher, name);
		} catch (NullPointerException e) {
			WebUtils.setError(req, "Invalid classroom name");
		}
		WebUtils.pageRedirect(req, res, "home");
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			// checks if logged in
			String token = AuthController.getToken(req).orElse("");
			Optional<Account> possibleAccount = Session.getAccountFromToken(token);

			if (possibleAccount.isPresent()) {
				Account acc = possibleAccount.get();
				req.setAttribute("account", acc);

				if (Student.isStudent(acc))
					handleStudentGet(req, res, Student.getFromAccount(acc));
				else handleTeacherGet(req, res, Teacher.getFromAccount(acc));

				return;
			}

			WebUtils.setError(req, "You must be logged in to access a home page");
		} catch (SQLException e) {
			WebUtils.setError(req, "Internal Server Error");
			e.printStackTrace();
		} catch (Exception e) {
			WebUtils.setError(req, "Unknown Error");
			e.printStackTrace();
		}

		AuthController.applyToken(res, "");
		WebUtils.pageRedirect(req, res, "account");
	}

	private void handleTeacherGet(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws ServletException, IOException, SQLException {
		req.setAttribute("teacher", teacher);

		switch (req.getServletPath()) {
			case "/classroom": {
				Optional<Classroom> classroom = teacher.getClassroomById(req.getParameter("id"));

				if (classroom.isPresent()) {
					req.setAttribute("classroom", classroom.get());
					WebUtils.redirect(req, res, WebUtils.webInf("classroom.teacher.jsp"));
				} else {
//					WebUtils.pageRedirect(req, res, "home");
					WebUtils.setError(req, "Classroom does not exist");
					WebUtils.pageRedirect(req, res, "home");
				}

				break;
			}

			case "/home": {
				req.setAttribute("classrooms", teacher.getClassrooms());
				WebUtils.redirect(req, res, WebUtils.webInf("home.teacher.jsp"));
				break;
			}

			default:
				WebUtils.pageRedirect(req, res, "home");
				break;
		}
	}

	private void handleStudentGet(HttpServletRequest req, HttpServletResponse res, Student student) throws ServletException, IOException, SQLException {
		req.setAttribute("student", student);

		req.setAttribute("classroom", student.getClassroom());
		WebUtils.redirect(req, res, WebUtils.webInf("home.student.jsp"));
	}
}
