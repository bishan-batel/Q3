package utils;

/**
 * Static util class for fallbacking values
 */
public final class Fallback {
	public static <T> T forNull(T test, T backup) {
		return test == null ? backup : test;
	}

	public static String forNullOrWhitespace(String test, String backup) {
		return test == null || test.isEmpty() ? backup : test;
	}

	private Fallback() {
	}

	public static void main(String[] args) {
		String a = "its so sad steve jobs died of ligma", b = "";
		System.out.println(Fallback.forNullOrWhitespace(a, "bruh"));
		System.out.println(Fallback.forNullOrWhitespace(null, "Val was null"));
		System.out.println(Fallback.forNullOrWhitespace(b, "Gaming"));
	}
}
