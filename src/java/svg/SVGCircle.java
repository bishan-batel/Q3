package svg;

public class SVGCircle extends SVGObject<SVGCircle>
{

  public SVGCircle()
  {
    super("circle");
  }

  public SVGCircle pos(double x, double y)
  {
    writeProperty("cx", x);
    return writeProperty("cy", y);
  }

  public SVGCircle size(double r)
  {
    return writeProperty("r", r);
  }
}
