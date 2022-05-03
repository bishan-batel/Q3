package auth;

import javax.security.auth.login.AccountException;

import static java.lang.String.format;

public class DuplicateEmailException extends AccountException
{
	public DuplicateEmailException(String email)
	{
		super(format("Email %s already has an account", email)); }
}
