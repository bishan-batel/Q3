package svg;

public class SVGText extends SVGObject<SVGText> {

	public SVGText() {
		this("");
	}

	public SVGText(String txt) {
		super("text");
		writeInner(txt);
	}

	public SVGText at(double x, double y) {
		writeProperty("x", x);
		return writeProperty("y", y);
	}

	public SVGText write(String text) {
		return writeInner(text);
	}

}
