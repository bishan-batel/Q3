package auth.account;

import db.Db;
import db.SQLDb;
import utils.Guard;
import utils.WebUtils;

import java.sql.SQLException;

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

	public static Classroom createFor(Teacher teacher, String name) throws SQLException {
		Guard.againstNull(teacher);

		String id = WebUtils.randomUID();

		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.insert(Db.CLASSROOMS, id, teacher.getUid(), name);
		}

		return new Classroom(id, teacher.getUid(), name);
	}
}
