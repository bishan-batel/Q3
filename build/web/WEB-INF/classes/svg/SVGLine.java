package svg;

public class SVGLine extends SVGObject<SVGLine>
{

  public SVGLine()
  {
    super("line");
  }

  public SVGLine from(double x, double y)
  {
    writeProperty("x1", x);
    return writeProperty("y1", y);
  }

  public SVGLine to(double x, double y)
  {
    writeProperty("x2", x);
    return writeProperty("y2", y);
  }

}
