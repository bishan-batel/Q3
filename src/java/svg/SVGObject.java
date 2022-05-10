package svg;

import static java.lang.String.format;

public abstract class SVGObject<T> {

	protected final String elementName;
	protected final StringBuilder properties;
	protected final StringBuilder inner;

	public SVGObject(String name) {
		properties = new StringBuilder();
		elementName = name;
		inner = new StringBuilder();
	}

	public T writeInner(String data) {
		inner.append(data);
		return (T) this;
	}

	public T writeProperty(String name, Object val) {
		properties.append(format("%s=\"%s\" ", name, val.toString()));
		return (T) this;
	}

	public T style(SVGStyle style) {
		return writeProperty("style", style);
	}

	public T className(String className) {
		return writeProperty("class", className);
	}

	@Override
	public String toString() {
		if (inner.toString().isEmpty()) {

			return format("<%s %s/>", elementName, properties);
		}
		return format("<%s %s>%s</%s>", elementName, properties, inner,
						elementName);
	}
}
