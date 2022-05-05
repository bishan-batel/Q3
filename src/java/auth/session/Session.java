package auth.session;

import auth.account.Account;
import db.Db;
import db.SQLDb;
import utils.WebUtils;

import java.sql.SQLException;
import java.util.Optional;

public final class Session {
	public final String token, uid;

	public Session(String[] row) {
		this(Db.SESSIONS.getString(row, "token"), Db.SESSIONS.getString(row, "uid"));
	}

	Session(String token, String uid) {
		this.token = token;
		this.uid = uid;
	}

	public String getToken() {
		return token;
	}

	public String getUid() {
		return uid;
	}

	public static Session createFor(String uid) throws SQLException {
		String token = WebUtils.randomUID();
		try (SQLDb db = new SQLDb(Db.NAME)) {
			db.insert(Db.SESSIONS, token, uid);
		}
		return new Session(token, uid);
	}

	public static Session createFor(Account account) throws SQLException {
		return createFor(account.getUid());
	}

	public static Optional<Account> getAccountFromSession(Session session) throws SQLException {
		return getAccountFromToken(session.uid);
	}

	public static Optional<Account> getAccountFromToken(String token) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {

			// SELECT * FROM Users WHERE UID IN (SELECT uid FROM Sessions WHERE token=?)
			String[][] rows = db.selectWhere(
							Db.USERS,
							String.format(
											"id in (SELECT uid from %s WHERE token=?) LIMIT 1",
											Db.SESSIONS.getName()
							),
							token
			);
			if (rows.length == 0) return Optional.empty();

			return Optional.of(new Account(rows[0]));
		}
	}

	public static boolean isValidSession(String token) throws SQLException {
		try (SQLDb db = new SQLDb(Db.NAME)) {
			return db.selectWhere(Db.SESSIONS, "token=? LIMIT 1", token).length > 0;
		}
	}
}
