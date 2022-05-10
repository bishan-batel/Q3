package auth;

import auth.DuplicateEmailException;
import auth.InvalidPasswordException;
import auth.SaltedPassword;
import auth.account.Account;
import auth.account.Classroom;
import auth.account.Teacher;
import auth.session.Session;
import db.Db;
import db.SQLDb;
import utils.WebUtils;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.Optional;

/// Singleton class
public final class Auth {

	public enum AccountType {
		TEACHER, STUDENT
	}

	public static Session login(String email, String password)
					throws InvalidPasswordException, AccountNotFoundException, SQLException {
		Optional<Account> possibleAccount = getAccountWithEmail(email);
		if (!possibleAccount.isPresent()) {
			throw new AccountNotFoundException();
		}

		// unwraps
		Account account = possibleAccount.get();

		// gets stored password
		SaltedPassword stored = account.getPassword();

		// creates a new salted password with stored salt with incoming password
		SaltedPassword request = new SaltedPassword("", stored.getSalt());
		request.setHash(password);

		if (!stored.equals(request)) {
			throw new InvalidPasswordException();
		}

		return Session.createFor(account);
	}

	public static Session register(String email, String password, String firstName,
	                               String lastName, AccountType accountType) throws DuplicateEmailException, SQLException {
		// checks if account exists
		if (getAccountWithEmail(email).isPresent() || email.length() > 254) {
			throw new DuplicateEmailException(email);
		}

		SaltedPassword salted = new SaltedPassword(password);
		String uid = WebUtils.randomUID();

		// connects to db
		try (SQLDb db = new SQLDb(Db.NAME)) {

			// inserts user
			db.insert(Db.USERS, uid, email, salted.getHash(), salted.getSalt(), firstName, lastName);

			// inserts teacher or student
			if (accountType == AccountType.TEACHER) {
				db.insert(Db.TEACHERS, uid);
			} else if (accountType == AccountType.STUDENT) {
				db.insert(Db.STUDENTS, uid, 0, "");
			}
		}

		return Session.createFor(uid);
	}

	public static Optional<Account> getAccountWithEmail(String email) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			String[][] results = db.selectWhere(Db.USERS, "email=?", email);

			db.close();
			if (results.length == 0) {
				return Optional.empty();
			}
			return Optional.of(new Account(results[0]));
		}
	}

	public static void main(String[] args) throws DuplicateEmailException, SQLException, AccountNotFoundException, InvalidPasswordException {
//		Session session = register("bruh@gmail", "pass", "Kishan", "Patel", AccountType.TEACHER);
		Session session = login("bruh@gmail", "pass");

		Account acc = Session.getAccountFromSession(session).get();

		System.out.println("Logged in");
		for (Classroom classroom : Teacher.getFromAccount(acc).getClassrooms()) {
			System.out.println(classroom);
		}
	}
}
