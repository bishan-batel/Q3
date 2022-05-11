package auth.account;

import db.Db;
import db.SQLDb;
import utils.Guard;
import utils.WebUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
			JoinRequest[] req = Arrays.stream(db.selectWhere(Db.JOIN_REQUESTS, "classroomId=?", getId()))
							.map(JoinRequest::new)
							.toArray(JoinRequest[]::new);
			db.close();
			return req;
		}
	}

	public Student[] getStudents() throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(
							Db.STUDENTS,
							"classroomId=?",
							getId()
			);
			db.close();

			// I would use streams for this but the student constructor having an
			// exception mucks things up
			List<Student> students = new ArrayList<>();

			for (String[] row : results)
				students.add(new Student(row));

			return students.toArray(new Student[0]);
		}
	}

	public int getStudentCount() throws SQLException {
		return getStudents().length;
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

	public Assignment[] getAssignments() throws SQLException {
		String[][] result;
		try (SQLDb db = new SQLDb(Db.NAME)) {
			result = db.selectWhere(Db.ASSIGNMENTS, "classroomId=? ORDER BY dateDue DESC", getId());
		}
		System.out.println(Arrays.deepToString(result));
		return Arrays.stream(result)
						.map(Assignment::new)
						.toArray(Assignment[]::new);
	}

	public static Classroom createFor(Teacher teacher, String name) throws SQLException {
		Guard.forNull(teacher);

		String id = WebUtils.randomUID().substring(0, 6);

		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.insert(Db.CLASSROOMS, id, teacher.getUid(), name);
		}

		return new Classroom(id, teacher.getUid(), name);
	}

	public static Optional<Classroom> getById(String id) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] result = db.selectWhere(Db.CLASSROOMS, "id=? LIMIT 1", id);
			if (result.length == 0) {
				db.close();
				return Optional.empty();
			}
			db.close();
			return Optional.of(new Classroom(result[0]));
		}
	}

}
