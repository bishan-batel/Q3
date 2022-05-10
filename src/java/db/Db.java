/*
 * Kishan Patel
 * Oct 29.
 * This class is used to install the database and neccesary parts onto the
 * users machine
 */
package db;

import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.Arrays;

@SuppressWarnings("unused")
public final class Db {
	public static final int PORT = 3306;

	public static final String SQL_USER = "root";
	public static final String SQL_PASS = "mysql1";

	public static final String NAME = "Q3";

	public static final SQLTableInfo USERS = new SQLTableInfo(
					"Users", // name
					"id varchar(32) PRIMARY KEY", // ID
					"email varchar(254) NOT NULL", // email
					"passwordHash varchar(256)",
					"passwordSalt varchar(256)",
					"firstName varchar(256)",
					"lastName varchar(256)"
	);

	public static final SQLTableInfo SESSIONS = new SQLTableInfo(
					"Sessions",
					"token varchar(256) PRIMARY KEY",
					"uid varchar(32) NOT NULL"
	);

	public static final SQLTableInfo TEACHERS = new SQLTableInfo(
					"Teachers",
					"uid varchar(32) PRIMARY KEY"
	);

	public static final SQLTableInfo STUDENTS = new SQLTableInfo(
					"Students",
					"uid varchar(32) PRIMARY KEY",
					"score int NOT NULL",
					"classroomId varchar(256)"
	);

	public static final SQLTableInfo CLASSROOMS = new SQLTableInfo(
					"Classrooms",
					"id varchar(32) PRIMARY KEY",
					"ownerId varchar(32) NOT NULL",
					"name varchar(256) NOT NULL"
	);

	public static final SQLTableInfo JOIN_REQUESTS = new SQLTableInfo(
					"JoinRequests",
					"uid varchar(32) PRIMARY KEY",
					"classroomId varchar(32) NOT NULL"
	);

	public static final SQLTableInfo ASSIGNMENTS = new SQLTableInfo(
					"Assignments",
					"id varchar(32) PRIMARY KEY",
					"classroomId varchar(32) NOT NULL",
					"type varchar(256) NOT NULL",
					"minimumQuestions int NOT NULL",
					"minimumAccuracy double NOT NULL",
					"dateDue date",
					"dateAdded date NOT NULL"
	);

	public static final SQLTableInfo ASSIGNMENT_LOGS = new SQLTableInfo(
					"AssignmentLogs",
					"assignmentId varchar(32) NOT NULL",
					"studentId varchar(32) NOT NULL",
					"didCorrect int NOT NULL",
					"completedDate date NOT NULL"
	);


	public static void main(String[] args) throws SQLException {
		SQLDb.createNewDb(NAME).close();

		try (SQLDb db = new SQLDb(NAME)) {
			// filters out every field in class that is
			// static & final & of type SQLTableInfo
			Arrays.stream(Db.class.getDeclaredFields())
							.filter(field -> Modifier.isStatic(field.getModifiers())
											&& Modifier.isFinal(field.getModifiers())
											&& field.getType().equals(SQLTableInfo.class)
							)
							// loop for each field & recreate table
							.forEach(field ->
							{
								try {
									db.recreateTable((SQLTableInfo) field.get(null));
								} catch (SQLException | IllegalAccessException ignored) {
								}
							});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
