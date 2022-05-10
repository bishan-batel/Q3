package auth.account;

import db.Db;
import db.SQLDb;
import question.ProblemType;
import utils.WebUtils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

import utils.Fallback;

public class Assignment {

	private final String id, classroomId;
	private final ProblemType type;
	private final Date dateAdded, dateDue;
	private final int minimumQuestions;
	private final double minimumAccuracy;

	Assignment(String[] row) {
		this(
						Db.ASSIGNMENTS.getString(row, "id"),
						Db.ASSIGNMENTS.getString(row, "classroomId"),
						Db.ASSIGNMENTS.getString(row, "type"),
						Integer.parseInt(Db.ASSIGNMENTS.getString(row, "minimumQuestions")),
						Double.parseDouble(Db.ASSIGNMENTS.getString(row, "minimumAccuracy")),
						Date.valueOf(Db.ASSIGNMENTS.getString(row, "dateAdded")),
						Date.valueOf(Db.ASSIGNMENTS.getString(row, "dateDue"))
		);
	}

	Assignment(
					String id,
					String classroomId,
					String type,
					int minimumQuestions,
					double minimumAccuracy,
					Date dateAdded,
					Date dateDue
	) {
		this.id = id;
		this.classroomId = classroomId;
		this.type = ProblemType.tryParse(type).orElse(ProblemType.SINUSOIDAL_GRAPH);
		this.minimumQuestions = minimumQuestions;
		this.minimumAccuracy = minimumAccuracy;
		this.dateAdded = dateAdded;
		this.dateDue = dateDue;
	}

	public String getId() {
		return id;
	}

	public String getClassroomId() {
		return classroomId;
	}

	public ProblemType getType() {
		return type;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public int getMinimumQuestions() {
		return minimumQuestions;
	}

	public double getMinimumAccuracy() {
		return minimumAccuracy;
	}

	public Date getDateDue() {
		return dateDue;
	}

	public Classroom getClassroom() throws SQLException {
		return Classroom.getById(classroomId).orElseThrow(IllegalStateException::new);
	}

	public double getAccuracy(Student student) throws SQLException {
		return getAccuracy(student.getUid());
	}

	public double getAccuracy(String studentId) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			ResultSet res = db.execPreparedWithResult(
							String.format(
											"SELECT SUM(didCorrect)/COUNT(*) FROM %s WHERE assignmentId=? AND studentId=? AND completedDate<?",
											Db.ASSIGNMENT_LOGS.getName()
							),
							getId(),
							studentId,
							getDateDue()
			);

			db.close();
			if (!res.next()) return 0;
			return Fallback.forNull(res.getObject(1, Double.class), 0d);
		}
	}

	public int getQuestionsDone(Student student) throws SQLException {
		return getQuestionsDone(student.getUid());
	}

	public int getQuestionsDone(String studentId) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			ResultSet res = db.execPreparedWithResult(
							String.format(
											"SELECT COUNT(*) FROM %s WHERE assignmentId=? AND studentId=? AND completedDate<?",
											Db.ASSIGNMENT_LOGS.getName()
							),
							getId(),
							studentId,
							getDateDue()
			);
			db.close();

			if (!res.next()) return 0;
			return Fallback.forNull(res.getObject(1, Integer.class), 0);
		}
	}

	public void delete() throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.deleteWhere(Db.ASSIGNMENT_LOGS, "assignmentId=?", getId());
			db.deleteWhere(Db.ASSIGNMENTS, "id=?", getId());
		}
	}

	public static Optional<Assignment> getById(String id) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] res = db.selectWhere(Db.ASSIGNMENTS, "id=?", id);
			db.close();
			if (res.length == 0) return Optional.empty();
			return Optional.of(new Assignment(res[0]));
		}
	}

	public static void createFor(
					Classroom classroom,
					ProblemType type,
					int minimumQuestions,
					double minimumAccuracy,
					Date dateDue
	) throws SQLException {
		String id = WebUtils.randomUID();
		Date dateAdded = Date.valueOf(LocalDate.now());

		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.insert(
							Db.ASSIGNMENTS,
							id,
							classroom.getId(),
							type.name(),
							minimumQuestions,
							minimumAccuracy,
							dateDue,
							dateAdded
			);
		}
	}

}
