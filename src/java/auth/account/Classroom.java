package auth.account;

import db.Db;
import db.SQLDb;
import utils.Guard;
import utils.WebUtils;

import java.sql.SQLException;
import java.util.Arrays;

public class Classroom {
	private final String id, ownerId, name;

	Classroom(String[] row) {
		this(
						Db.CLASSROOMS.getString(row, "id"),
						Db.CLASSROOMS.getString(row, "ownerId"),
						Db.CLASSROOMS.getString(row, "name")
		);
	}

	Classroom(String id, String ownerId, String name) {
		this.id = id;
		this.ownerId = ownerId;
		this.name = name;
	}


	public String getId() {
		return id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public String getName() {
		return name;
	}

	public JoinRequest[] getJoinRequests() throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			return Arrays.stream(db.selectWhere(Db.JOIN_REQUESTS, "classroomId=?", getId()))
							.map(JoinRequest::new)
							.toArray(JoinRequest[]::new);
		}
	}

	public int getStudentCount() throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			return db.selectWhere(
							Db.STUDENTS,
							"classroom=?",
							getId()
			).length;
		}
	}

	/*
	 * Deletes a classroom by id
	 */
	public static void deleteById(String id) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.deleteWhere(Db.ASSIGNMENTS, "classroomId=?", id);
			db.deleteWhere(Db.CLASSROOMS, "id=?", id);

			db.updateWhere(
							Db.STUDENTS,
							"classroomId=null",
							"classroomId=?",
							id
			);
		}
	}

	public static Classroom createFor(Teacher teacher, String name) throws SQLException {
		Guard.againstNull(teacher);

		String id = WebUtils.randomUID().substring(0, 6);

		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.insert(Db.CLASSROOMS, id, teacher.getUid(), name);
		}

		return new Classroom(id, teacher.getUid(), name);
	}

	public static boolean exists(String id) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			return db.selectWhere(Db.CLASSROOMS, "id=? LIMIT=1", id).length > 0;
		}
	}
}
