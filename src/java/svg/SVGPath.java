package svg;

import org.apache.derby.iapi.jdbc.JDBCBoot;

public class SVGPath extends SVGObject<SVGPath> {
	private StringBuilder dBuilder;

	public SVGPath() {
		super("path");
		dBuilder = new StringBuilder();
	}

	public SVGPath close() {
		dBuilder.append("Z");
		return this;
	}

	public SVGPath line(double x, double y) {
		dBuilder.append(String.format("L %f %f ", x, y));
		return this;
	}

	public SVGPath lineRelative(double x, double y) {
		dBuilder.append(String.format("l %f %f ", x, y));
		return this;
	}

	public SVGPath move(double x, double y) {
		dBuilder.append(String.format("M %f %f ", x, y));
		return this;
	}

	public SVGPath moveRelative(double x, double y) {
		dBuilder.append(String.format("m %f %f ", x, y));
		return this;
	}

	@Override
	public String toString() {
		int oldLength = properties.length();

		writeProperty("d", dBuilder);
		String ele = super.toString();
		properties.delete(oldLength, oldLength + dBuilder.length());
		return ele;
	}
}
