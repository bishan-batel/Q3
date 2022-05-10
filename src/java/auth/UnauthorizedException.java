package auth;

public class UnauthorizedException extends Exception {
	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String author, String action) {
		super(String.format("User %s is not allowed to %s", author, action));
	}
}
