package svg;

import static java.lang.String.format;

public class SVGStyle {
	public StringBuilder builder;

	public SVGStyle() {
		builder = new StringBuilder();
	}

	public SVGStyle css(String name, Object prop) {
		builder.append(format("%s:%s;", name, prop));
		return this;
	}

	public SVGStyle fill(String col) {
		return css("fill", col);
	}

	public SVGStyle stroke(String col) {
		return css("stroke", col);
	}

	public SVGStyle font(String font) {
		return css("font", font);
	}

	public SVGStyle fontSize(String size) {
		return css("font-size", size);
	}

	public SVGStyle fontColor(String col) {
		return css("color", col);
	}

	public SVGStyle fontWeight(String weight) {
		return css("font-weight", weight);
	}

	public String toString() {
		return builder.toString();
	}
}
