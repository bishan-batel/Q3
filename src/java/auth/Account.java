package auth;

import auth.session.Session;
import db.SQLDb;
import db.Db;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.UUID;

import static db.Db.*;
import static java.lang.String.format;

public class Account {

    private final String uid;
    private final String email;
    private final String firstName, middleName, lastName;
    private final SaltedPassword password;

    Account(String[] row) {
        // parses UID and email out of row
        uid = USERS.getString(row, "id");
        email = USERS.getString(row, "email");

        firstName = USERS.getString(row, "firstName");
        middleName = USERS.getString(row, "middleName");
        lastName = USERS.getString(row, "lastName");

        password = new SaltedPassword(
                USERS.getString(row, "passwordHash"),
                USERS.getString(row, "passwordSalt")
        );
    }

    public static String uuidToEmail(String uuid) throws SQLException, AccountNotFoundException {
        try (SQLDb db = new SQLDb(Db.NAME)) {
            String[][] rows = db.selectWhere(USERS, "id=?", uuid);
            if (rows.length == 0) {
                throw new AccountNotFoundException();
            }
            return USERS.getString(rows[0], "email");
        }
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

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public SaltedPassword getPassword() {
        return password;
    }
}
