import auth.UnauthorizedException;
import auth.account.*;
import auth.session.Session;
import question.Problem;
import question.ProblemType;
import utils.Guard;
import utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author bishan
 */
@WebServlet(
				name = "HomeController",
				loadOnStartup = 1,
				urlPatterns = {
								"/home",
								"/classroom",
								"/assignment",

								// Teacher
								"/home-create-classroom",
								"/home-delete-classroom",
								"/home-create-assignment",
								"/home-delete-assignment",
								"/home-accept-join-req",
								"/home-reject-join-req",

								// Student
								"/home-send-join-req",
								"/home-cancel-join-req",
								"/assignment-submit",
				}
)
public class HomeController extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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
			WebUtils.setError(req, "Internal Server Error");
			WebUtils.pageRedirect(req, res, "home");
			e.printStackTrace();
		}

	}

// Student Post Handling

	private void handleStudentPost(HttpServletRequest req, HttpServletResponse res, Student student) throws ServletException, IOException, SQLException {
		switch (req.getServletPath()) {
			case "/home-send-join-req":
				sendJoinRequest(req, res, student);
				return;

			case "/home-cancel-join-req":
				cancelJoinReq(req, res, student);
				return;

			case "/assignment-submit":
				submitAssignment(req, res, student);
				return;

			default:
				WebUtils.pageRedirect(req, res, "home");
		}
	}

	private void submitAssignment(HttpServletRequest req, HttpServletResponse res, Student student) throws ServletException, IOException, SQLException {
		try {
			String assignmentId = req.getParameter("id"), response = req.getParameter("response");
			Classroom classroom = student.getClassroom().orElseThrow(NullPointerException::new);

			Guard.forNullOrWhitespace(assignmentId, response);

			Optional<Assignment> assignmentOption = Assignment.getById(assignmentId);
			if (!assignmentOption.isPresent()) {
				WebUtils.setError(req, "Assignment does not exist");
				throw new UnauthorizedException();
			}

			Assignment assignment = assignmentOption.get();

			// ensures access
			if (!Objects.equals(assignment.getClassroomId(), classroom.getId()))
				throw new UnauthorizedException();

			// check for problem generator
			Problem problem = (Problem) req.getSession(true).getAttribute("problem");
			if (problem == null) {
				WebUtils.pageRedirect(req, res, String.format("assignment?id=%s", assignmentId));
				return;
			}
			boolean isCorrect = problem.isCorrect(response);
			AssignmentLog.addLog(assignmentId, student.getUid(), isCorrect, Date.valueOf(LocalDate.now()));

			WebUtils.pageRedirect(req, res, String.format("assignment?id=%s", assignmentId));
		} catch (NullPointerException | IllegalArgumentException e) {
			WebUtils.setError(req, "Invalid data");
		} catch (UnauthorizedException e) {
			WebUtils.setError(req, "You do not have access to this assignment");
		}
		WebUtils.pageRedirect(req, res, "home");
	}

	private void sendJoinRequest(HttpServletRequest req, HttpServletResponse res, Student student) throws ServletException, IOException, SQLException {
		try {
			String classroomId = req.getParameter("classroomId");
			Guard.forNullOrWhitespace(classroomId);
			student.sendJoinRequest(classroomId);
			WebUtils.setError(req, null);
		} catch (NullPointerException | IllegalArgumentException nfe) {
			WebUtils.setError(req, "Invalid join code");
		}
		WebUtils.pageRedirect(req, res, "home");
	}

	private void cancelJoinReq(HttpServletRequest req, HttpServletResponse res, Student student) throws ServletException, IOException, SQLException {
		JoinRequest.clearForUser(student);
		WebUtils.pageRedirect(req, res, "home");
	}


	// Teacher Post Handing


	private void handleTeacherPost(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws ServletException, IOException, SQLException {
		switch (req.getServletPath()) {
			case "/home-create-classroom":
				createClassroom(req, res, teacher);
				return;

			case "/home-delete-classroom":
				deleteClassroom(req, res, teacher);
				return;

			case "/home-accept-join-req":
				acceptJoinRequest(req, res, teacher);
				return;

			case "/home-reject-join-req":
				rejectJoinRequest(req, res, teacher);
				return;

			case "/home-create-assignment":
				createAssignment(req, res, teacher);
				break;

			case "/home-delete-assignment":
				deleteAssignment(req, res, teacher);
				break;

			default:
				WebUtils.pageRedirect(req, res, "home");
		}
	}

	private void deleteClassroom(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws SQLException, ServletException, IOException {
		try {
			String classroomId = req.getParameter("classroomId");
			Guard.forNullOrWhitespace(classroomId);

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
			Guard.forNullOrWhitespace(name);

			Classroom.createFor(teacher, name);
		} catch (NullPointerException e) {
			WebUtils.setError(req, "Invalid classroom name");
		}
		WebUtils.pageRedirect(req, res, "home");
	}

	private void acceptJoinRequest(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws SQLException, ServletException, IOException {
		String studentId = req.getParameter("studentId");

		try {
			Guard.forNullOrWhitespace(studentId);
			Student student = Student.getFromAccount(Account.getFromId(studentId).orElseThrow(IllegalArgumentException::new));
			JoinRequest joinRequest = student.getJoinRequest().orElseThrow(IllegalArgumentException::new);

			if (!teacher.ownsClassroom(joinRequest.getClassroomId()))
				throw new UnauthorizedException();

			joinRequest.accept();
			WebUtils.setError(req, null);
			WebUtils.pageRedirect(req, res, String.format("classroom?id=%s", joinRequest.getClassroomId()));
			return;
		} catch (IllegalArgumentException | NullPointerException e) {
			WebUtils.setError(req, "Invalid join request");
		} catch (UnauthorizedException e) {
			WebUtils.setError(req, "You are not authorized to manage requests for this classroom");
		}
		WebUtils.pageRedirect(req, res, "home");
	}

	private void rejectJoinRequest(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws SQLException, ServletException, IOException {
		String studentId = req.getParameter("studentId");

		try {
			Guard.forNullOrWhitespace(studentId);
			Student student = Student.getFromAccount(Account.getFromId(studentId).orElseThrow(IllegalArgumentException::new));
			JoinRequest joinRequest = student.getJoinRequest().orElseThrow(IllegalArgumentException::new);

			if (!teacher.ownsClassroom(joinRequest.getClassroomId()))
				throw new UnauthorizedException();

			joinRequest.reject();
			WebUtils.setError(req, null);
			WebUtils.pageRedirect(req, res, String.format("classroom?id=%s", joinRequest.getClassroomId()));
			return;
		} catch (IllegalArgumentException | NullPointerException e) {
			WebUtils.setError(req, "Invalid join request");
		} catch (UnauthorizedException e) {
			WebUtils.setError(req, "You are not authorized to manage requests for this classroom");
		}
		WebUtils.pageRedirect(req, res, "home");
	}

	private void createAssignment(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws ServletException, IOException, SQLException {
		String classroomId = req.getParameter("classroomId");
		try {
			String type = req.getParameter("type");
			Date dueDate = Date.valueOf(req.getParameter("due"));
			int minQuestions = Integer.parseInt(req.getParameter("minimumQuestions"));
			double minAccuracy = Double.parseDouble(req.getParameter("minimumAccuracy"));

			// validating the input
			Guard.forNullOrWhitespace(classroomId);
			Guard.whitelist(type, (Object[]) Arrays.stream(ProblemType.values()).map(ProblemType::name).toArray(String[]::new));
			Guard.forRangeInclusive(minQuestions, 1, 100);
			Guard.forRangeInclusive(minAccuracy, 0, 1);

			if (!teacher.ownsClassroom(classroomId)) throw new UnauthorizedException();
			Classroom classroom = Classroom.getById(classroomId).orElseThrow(IllegalArgumentException::new);
			Assignment.createFor(classroom, ProblemType.valueOf(type), minQuestions, minAccuracy, dueDate);
			WebUtils.setError(req, null);
		} catch (NullPointerException | IllegalArgumentException e) {
			WebUtils.setError(req, "Invalid form data");
		} catch (UnauthorizedException e) {
			WebUtils.setError(req, "You are not allowed to manage assignments");
		}
		WebUtils.pageRedirect(req, res, String.format("classroom?id=%s", classroomId));
	}

	private void deleteAssignment(HttpServletRequest req, HttpServletResponse res, Teacher teacher) throws ServletException, IOException, SQLException {
		String classroomId = req.getParameter("classroomId");
		String assignmentId = req.getParameter("assignmentId");
		try {
			Guard.forNullOrWhitespace(classroomId, assignmentId);

			if (!teacher.ownsClassroom(classroomId)) throw new UnauthorizedException();

			Assignment assignment = Assignment.getById(assignmentId).orElseThrow(NullPointerException::new);
			assignment.delete();

		} catch (NullPointerException | IllegalArgumentException e) {
			WebUtils.setError(req, "Invalid Form Data");
		} catch (UnauthorizedException e) {
			WebUtils.setError(req, "Unauthorized");
		}
		WebUtils.pageRedirect(req, res, String.format("classroom?id=%s", classroomId));
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
			WebUtils.pageRedirect(req, res, "account");
			return;
		} catch (SQLException e) {
			WebUtils.setError(req, "Internal Server Error");
			e.printStackTrace();
		} catch (Exception e) {
			WebUtils.setError(req, "Unknown Error");
			e.printStackTrace();
		}

//		AuthController.applyToken(res, "");
		WebUtils.redirect(req, res, WebUtils.webInf("err.jsp"));
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

			case "/assignment": {
				Optional<Assignment> assignment = Assignment.getById(req.getParameter("id"));

				if (assignment.isPresent() && teacher.ownsClassroom(assignment.get().getClassroomId())) {
					req.setAttribute("teacher", teacher);
					req.setAttribute("assignment", assignment.get());
					WebUtils.redirect(req, res, WebUtils.webInf("assignment.teacher.jsp"));
					break;
				}
				WebUtils.setError(req, "Assignment does not exist");
				WebUtils.pageRedirect(req, res, "home");
				break;
			}

			default:
				WebUtils.pageRedirect(req, res, "home");
				break;
		}
	}

	private void handleStudentGet(HttpServletRequest req, HttpServletResponse res, Student student) throws ServletException, IOException, SQLException {
		Classroom classroom = student.getClassroom().orElse(null);
		req.setAttribute("classroom", classroom);
		req.setAttribute("student", student);

		switch (req.getServletPath()) {
			case "/assignment": {
				if (classroom == null) break;

				String assignmentId = req.getParameter("id");

				Optional<Assignment> assignmentOption = Assignment.getById(assignmentId);
				if (!assignmentOption.isPresent()) {
					WebUtils.setError(req, "Assignment does not exist");
					break;
				}
				Assignment assignment = assignmentOption.get();

				// check that user has acces to assignment
				if (!Objects.equals(assignment.getClassroomId(), classroom.getId())) {
					WebUtils.setError(req, "You do not have access to this assignment");
					break;
				}
				req.setAttribute("assignment", assignment);

				Problem problem = assignment.getType().generate();
				req.getSession(true).setAttribute("problem", problem);
				problem.generate();
				WebUtils.redirect(req, res, WebUtils.webInf("assignment.jsp"));
				return;
			}

			case "/home":
				WebUtils.redirect(req, res, WebUtils.webInf("home.student.jsp"));
				return;
		}

		// default
		WebUtils.setError(req, "Page does not exist for this student");
		WebUtils.pageRedirect(req, res, "home");
	}

}
