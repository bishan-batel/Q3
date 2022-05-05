package auth.account;

import db.Db;
import db.SQLDb;
import utils.Guard;

import java.sql.SQLException;

public final class Student {
	private final Account account;
	private final String classroomId;

	@Deprecated
	private int score;


	Student(Account account, String[] row) {
		this.account = account;

		classroomId = Db.STUDENTS.getString(row, "classroomId");
		score = Integer.parseInt(Db.STUDENTS.getString(row, "score"));
	}

	public Account getAccount() {
		return account;
	}

	public void changeClassroom(String classroomId) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.updateWhere(
							Db.STUDENTS,
							"classroomId=?",
							"uid=?",
							classroomId,
							account.getUid()
			);
		}
	}

	public String getClassroomId() {
		return classroomId;
	}

	public int getScore() {
		return score;
	}

	public static Student getFromAccount(Account account) throws SQLException {
		Guard.againstNull(account);

		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(Db.STUDENTS, "uid=? LIMIT 1", account.getUid());
			if (results.length == 0)
				throw new IllegalArgumentException(String.format("Account %s is not a student", account));

			return new Student(account, results[0]);
		}
	}

	public static boolean isStudent(Account account) throws SQLException {
		Guard.againstNull(account);

		try (SQLDb db = new SQLDb(Db.NAME)) {
			return db.selectWhere(Db.STUDENTS, "uid=? LIMIT 1", account.getUid()).length > 0;
		}
	}
}
