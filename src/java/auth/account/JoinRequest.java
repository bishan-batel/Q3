package auth.account;

import db.Db;
import db.SQLDb;

import java.sql.SQLException;
import java.util.Optional;

public class JoinRequest {
	private final String uid;
	private String classroomId;
	private Student cachedStudent;

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

	public String getClassroomId() {
		return classroomId;
	}

	public Student getStudent() throws SQLException {
		return getStudent(true);
	}

	public Student getStudent(boolean allowCache) throws SQLException {
		if (cachedStudent != null && allowCache) {
			return cachedStudent;
		}
		Optional<Account> acc = Account.getFromId(getUid());
		if (acc.isPresent())
			return cachedStudent = Student.getFromAccount(acc.get());
		throw new IllegalStateException("Student does not exist for join request");
	}

	public String getUid() {
		return uid;
	}

	public void accept() throws SQLException {
		Student student = getStudent();
		clearForUser(student);
		student.changeClassroom(classroomId);
	}

	public void reject() throws SQLException {
		clearForUser(getStudent());
	}

	public static void clearForUser(Student student) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.deleteWhere(Db.JOIN_REQUESTS, "uid=?", student.getUid());
		}
	}
}
