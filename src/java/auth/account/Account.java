package auth.account;

import auth.SaltedPassword;
import auth.session.Session;
import db.SQLDb;
import db.Db;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static db.Db.*;
import static java.lang.String.format;

public class Account {

	private final String uid;
	private final String email;
	private final String firstName, lastName;
	private final SaltedPassword password;

	public Account(String[] row) {
		// parses UID and email out of row
		uid = USERS.getString(row, "id");
		email = USERS.getString(row, "email");

		firstName = USERS.getString(row, "firstName");
		lastName = USERS.getString(row, "lastName");

		password = new SaltedPassword(
						USERS.getString(row, "passwordHash"),
						USERS.getString(row, "passwordSalt")
		);
	}

	public String getUid() {
		return uid;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getFullName() {
		return String.format("%s %s", getFirstName(), getLastName());
	}

	public String getLastName() {
		return lastName;
	}

	public SaltedPassword getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "Account{" +
						"uid='" + uid + '\'' +
						", email='" + email + '\'' +
						", firstName='" + firstName + '\'' +
						", lastName='" + lastName + '\'' +
						", password=" + password +
						'}';
	}

	public static Optional<Account> getFromId(String id) throws SQLException {
		try (SQLDb db = new SQLDb(NAME)) {
			String[][] res = db.selectWhere(USERS, "id=?", id);
			db.close();
			if (res.length == 0) return Optional.empty();
			return Optional.of(new Account(res[0]));
		}
	}
}
