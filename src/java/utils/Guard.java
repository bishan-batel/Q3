package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class Guard {
	/**
	 * Asserts that value is within the range, throws excpetion if not
	 */
	public static void forRange(double val, double min, double max) {
		if (val < min || val > max) throw new IllegalArgumentException();
	}

	/**
	 * Asserts that value is within the range inclusively, throws excpetion if not
	 */
	public static void forRangeInclusive(double val, double min, double max) {
		if (val <= min || val >= max) throw new IllegalArgumentException();
	}

	public static void whitelist(Object map, Object... allowed) {
		for (Object obj : allowed) {
			if (Objects.equals(obj, map))
				return;
		}
		throw new IllegalArgumentException(map.toString());
	}

	/**
	 * Guarding for null should always have at least 1 argument
	 */
	@Deprecated
	public static void forNull() {
		throw new IllegalArgumentException();
	}

	public static void forNull(Object... objs) {
		for (Object o : objs) {
			if (o == null) throw new NullPointerException();
		}
	}

	/**
	 * Guarding should have at least 1 parameter
	 *
	 * @param objs
	 */
	@Deprecated
	public static void forNullOrWhitespace() {
		throw new IllegalArgumentException();
	}

	public static void forNullOrWhitespace(String... objs) {
		for (String o : objs) {
			if (o == null || o.isEmpty()) throw new NullPointerException();
		}
	}

	private Guard() {
	}
}
