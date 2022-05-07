package auth.account;

import db.Db;
import db.SQLDb;
import utils.Guard;

import java.sql.SQLException;
import java.util.Optional;

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

	public void requestToJoin(String id) {
	}

	public String getClassroomId() {
		return classroomId;
	}

	public String getUid() {
		return getAccount().getUid();
	}

	@Deprecated
	public int getScore() {
		return score;
	}

	public Optional<Classroom> getClassroom() throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(Db.CLASSROOMS, "id=?", getClassroomId());
			if (results.length == 0) return Optional.empty();
			return Optional.of(new Classroom(results[0]));
		}
	}

	public void sendJoinRequest(String classroomId) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			if (hasPendingJoinRequest(db)) {
				db.updateWhere(Db.JOIN_REQUESTS, "classroomId=?", "uid=?", classroomId, getUid());
			} else {
				db.insert(Db.JOIN_REQUESTS, getUid(), classroomId);
			}
		}
	}

	private boolean hasPendingJoinRequest(SQLDb db) throws SQLException {
		return db.selectWhere(Db.JOIN_REQUESTS, "uid=? LIMIT 1", getUid()).length == 0;
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
