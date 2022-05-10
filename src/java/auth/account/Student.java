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

	Student(String[] row) throws SQLException {
		classroomId = Db.STUDENTS.getString(row, "classroomId");
		score = Integer.parseInt(Db.STUDENTS.getString(row, "score"));
		account = Account.getFromId(Db.STUDENTS.getString(row, "uid")).orElseThrow(IllegalArgumentException::new);
	}

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

	/**
	 * C
	 *
	 * @param classroomId ID of the classroom to join
	 * @throws SQLException
	 */
	public void sendJoinRequest(String classroomId) throws SQLException {
		boolean hasJoinRequest = getJoinRequest().isPresent();
		try (SQLDb db = new SQLDb(Db.NAME)) {
			if (hasJoinRequest) {
				db.updateWhere(Db.JOIN_REQUESTS, "classroomId=?", "uid=?", classroomId, getUid());
			} else {
				db.insert(Db.JOIN_REQUESTS, getUid(), classroomId);
			}
		}
	}

	public Optional<JoinRequest> getJoinRequest() throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] res = db.selectWhere(Db.JOIN_REQUESTS, "uid=? LIMIT 1", getUid());
			if (res.length == 0) return Optional.empty();
			return Optional.of(new JoinRequest(res[0]));
		}
	}

	public static Student getFromAccount(Account account) throws SQLException {
		Guard.forNull(account);

		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(Db.STUDENTS, "uid=? LIMIT 1", account.getUid());
			db.close();
			if (results.length == 0)
				throw new IllegalArgumentException(String.format("Account %s is not a student", account));

			return new Student(account, results[0]);
		}
	}

	public static boolean isStudent(Account account) throws SQLException {
		Guard.forNull(account);

		try (SQLDb db = new SQLDb(Db.NAME)) {
			boolean b = db.selectWhere(Db.STUDENTS, "uid=? LIMIT 1", account.getUid()).length > 0;
			db.close();
			return b;
		}
	}


}
