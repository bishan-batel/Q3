package auth;

import auth.session.Session;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;

/// Singleton class
public final class Auth {

    /**
     * @param email
     * @param password
     * @return Returns session token
     */
    public static String login(
            String email,
            String password
    ) throws InvalidPasswordException, AccountNotFoundException, SQLException {
        // TODO FIX
        Account account = null;


        // gets stored password
        SaltedPassword stored = account.getPassword();

        // creates a new salted password with stored salt with incoming password
        SaltedPassword request = new SaltedPassword("", stored.getSalt());
        request.setHash(password);

        if (!stored.equals(request)) {
            throw new InvalidPasswordException();
        }

        return Session.create(account);
    }

    // TODO fix
    public static String register() {
        return "";
    }


}
