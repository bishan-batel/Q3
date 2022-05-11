package auth.account;

import db.Db;
import db.SQLDb;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;

public class AssignmentLog {
	private final String assignmentId, studentId;
	private final boolean didCorrect;
	private final Date completedDate;

	AssignmentLog(String[] row) {
		this(
						Db.ASSIGNMENT_LOGS.getString(row, "assignmentId"),
						Db.ASSIGNMENT_LOGS.getString(row, "studentId"),
						Integer.parseInt(Db.ASSIGNMENT_LOGS.getString(row, "didCorrect")) != 0,
						Date.valueOf(Db.ASSIGNMENT_LOGS.getString(row, "completedDate"))
		);
	}

	AssignmentLog(String id, String studentId, boolean didCorrect, Date completedDate) {
		this.assignmentId = id;
		this.studentId = studentId;
		this.didCorrect = didCorrect;
		this.completedDate = completedDate;
	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public String getStudentId() {
		return studentId;
	}

	public boolean isDidCorrect() {
		return didCorrect;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public static void addLog(String assignmentId, String studentId, boolean didCorrect, Date completedDate) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.insert(Db.ASSIGNMENT_LOGS, assignmentId, studentId, didCorrect ? 1 : 0, completedDate);
		}
	}


	public static AssignmentLog[] getOnTimeLogs(Assignment assignment, String studentId) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(
							Db.ASSIGNMENT_LOGS,
							"assignmentId=? AND studentId=? AND completedDate<?",
							assignment.getId(),
							studentId,
							assignment.getDateDue()
			);
			db.close();
			return Arrays.stream(results)
							.map(AssignmentLog::new)

							.toArray(AssignmentLog[]::new);
		}
	}

	public static AssignmentLog[] getOverdueLogs(Assignment assignment, String studentId) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(
							Db.ASSIGNMENT_LOGS,
							"assignmentId=? AND studentId=? AND completedDate>=?",
							assignment.getId(),
							studentId,
							assignment.getDateDue()
			);
			return Arrays.stream(results)
							.map(AssignmentLog::new)
							.toArray(AssignmentLog[]::new);
		}
	}
}
