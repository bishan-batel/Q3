package auth.account;

import db.Db;
import db.SQLDb;

import java.sql.SQLException;
import java.util.Optional;

public class JoinRequest {
	public final String uid;
	private String classroomId;

	JoinRequest(String[] row) {
		this(
						Db.JOIN_REQUESTS.getString(row, "uid"),
						Db.JOIN_REQUESTS.getString(row, "classroomId")
		);
	}

	JoinRequest(String uid, String classroomId) {
		this.uid = uid;
		this.classroomId = classroomId;
	}

	public Student getStudent() throws SQLException {
		Optional<Account> acc = Account.getFromId(getUid());
		if (acc.isPresent())
			return Student.getFromAccount(acc.get());
		throw new IllegalStateException("Student does not exist for join request");
	}

	public String getUid() {
		return uid;
	}

	public static void deleteAllForUser(Student student) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.deleteWhere(Db.JOIN_REQUESTS, "uid=?", student.getUid());
		}
	}
}
