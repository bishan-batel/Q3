package auth.account;

import db.Db;
import db.SQLDb;
import utils.Guard;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public final class Teacher {
	private final Account account;

	public Teacher(Account account) {
		Guard.forNull(account);

		this.account = account;
	}

	// Accessors
	public Account getAccount() {
		return account;
	}

	public String getUid() {
		return account.getUid();
	}

	public boolean ownsClassroom(String classroomId) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			return db.selectWhere(Db.CLASSROOMS, "ownerId=? AND id=? LIMIT 1", getUid(), classroomId).length > 0;
		}
	}

	public Classroom[] getClassrooms() throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(Db.CLASSROOMS, "ownerId=?", account.getUid());

			// maps results to array of Classroom objects
			return Arrays.stream(results).map(Classroom::new).toArray(Classroom[]::new);
		}
	}

	public Optional<Classroom> getClassroomById(String id) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(Db.CLASSROOMS, "ownerId=? AND id=? LIMIT 1", account.getUid(), id);

			if (results.length == 0)
				return Optional.empty();
			return Optional.of(new Classroom(results[0]));
		}
	}

	public static Teacher getFromAccount(Account account) throws SQLException {
		Guard.forNull(account);

		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(Db.TEACHERS, "uid=? LIMIT 1", account.getUid());
			if (results.length == 0)
				throw new IllegalArgumentException(String.format("Account %s is not a teacher", account));

			return new Teacher(account);
		} 
	}

	public static boolean isTeacher(Account account) throws SQLException {
		Guard.forNull(account);

		try (SQLDb db = new SQLDb(Db.NAME)) {
			return db.selectWhere(Db.TEACHERS, "uid=? LIMIT 1", account.getUid()).length > 0;
		}
	}

}
