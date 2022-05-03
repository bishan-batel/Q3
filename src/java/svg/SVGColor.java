package svg;

public final class SVGColor
{
	public static String rgb(double r, double g, double b)
	{
		return rgba(r, g, b, 255);
	}

	public static String rgba(double r, double g, double b, double a)
	{
		java.awt.Color col = new java.awt.Color((int) r, (int) g, (int) b,
			(int) a);
		return "#" + Integer.toHexString(col.getRGB()).substring(2);
	}
}
