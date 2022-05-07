package auth.account;

import db.Db;
import db.SQLDb;
import question.AssignmentType;
import utils.WebUtils;

import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public class Assignment {

	private final String id, classroomId;
	private final AssignmentType type;
	private final Date dateAdded;
	private int minimumQuestions;
	private double minimumAccuracy;
	private Optional<Date> dateDue;

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
		this.type = AssignmentType.tryParse(type).orElse(AssignmentType.RIGHT_TRIANGLE);
		this.minimumQuestions = minimumQuestions;
		this.minimumAccuracy = minimumAccuracy;
		this.dateAdded = dateAdded;
		this.dateDue = Optional.ofNullable(dateDue);
	}

	public String getId() {
		return id;
	}

	public String getClassroomId() {
		return classroomId;
	}

	public AssignmentType getType() {
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

	public Optional<Date> getDateDue() {
		return dateDue;
	}

	public static void createFor(
					Classroom classroom,
					AssignmentType type,
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
