package math;

import java.security.AccessControlContext;

/**
 * Math Vector class with Floating Point precision
 */
public class Vector2 implements Cloneable {
	public double x, y;

	public Vector2() {
		this(0, 0);
	}

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 scale(double s) {
		Vector2 v = clone();
		v.x *= s;
		v.y *= s;
		return v;
	}

	public Vector2 sub(Vector2 a) {
		Vector2 v = clone();
		v.x -= a.x;
		v.y -= a.y;
		return v;
	}

	public Vector2 add(Vector2 a) {
		Vector2 v = clone();
		v.x += a.x;
		v.y += a.y;
		return v;
	}

	public Vector2 midpoint(Vector2 other) {
		return other.add(this).scale(0.5);
	}

	public double distanceTo(Vector2 c) {
		return c.sub(this).length();
	}

	public double angleTo(Vector2 a) {
		return a.clone().sub(this).angle();
	}

	public double angle() {
		return Math.atan2(x, y);
	}

	public double length2() {
		return x * x + y * y;
	}

	public double length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public static Vector2 random() {
		double randomAng = Math.random() * 2f * Math.PI;
		return new Vector2(Math.sin(randomAng), Math.cos(randomAng));
	}


	public static Vector2 random(double length) {
		return random().scale(length);
	}


	@Override
	public Vector2 clone() {
		return new Vector2(x, y);
	}

	public Vector2 round() {
		return new Vector2(Math.round(x), Math.round(y));
	}
}
