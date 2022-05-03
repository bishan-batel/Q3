package auth.session;

import auth.Account;
import db.Db;
import db.SQLDb;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.UUID;

import static db.Db.SESSIONS;

public final class Session {

    public static String create(Account account) throws SQLException {
        String token = UUID.randomUUID().toString();
        try (SQLDb db = new SQLDb(Db.NAME)) {
            db.insert(SESSIONS, token, account.getUid());
        }
        return token;
    }

    public static Account getAccountFromToken(String token) throws SQLException,
            AccountNotFoundException {
        try (SQLDb db = new SQLDb(Db.NAME)) {
            String[][] rows = db.selectWhere(SESSIONS, "token=?", token);
            if (rows.length == 0) {
                throw new AccountNotFoundException();
            }

            // TODO fix
            return null;
//            return Account.retrieve(
//                    Account.uuidToEmail(SESSIONS.getString(rows[0], "uid"))
//            );
        }
    }
}
