package svg;

import static java.lang.String.format;

abstract class SVGObject<T>
{

	protected final String elementName;
	private final StringBuilder properties;
	private final StringBuilder inner;

	public SVGObject(String name)
	{
		properties = new StringBuilder();
		elementName = name;
		inner = new StringBuilder();
	}

	public T writeInner(String data)
	{
		inner.append(data);
		return (T) this;
	}

	public T writeProperty(String name, Object val)
	{
		properties.append(format("%s=\"%s\" ", name, val.toString()));
		return (T) this;
	}

	public T style(SVGStyle style)
	{
		return writeProperty("style", style.toString());
	}

	@Override
	public String toString()
	{
		if (properties.toString().isEmpty())
		{

			return format("<%s %s/>", elementName, inner);
		}
		return format("<%s %s>%s</%s>", elementName, properties, inner,
			elementName);
	}
}
