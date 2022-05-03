/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package svg;

/**
 *
 * @author bishan
 */
public class SVGRect extends SVGObject<SVGRect>
{

  public SVGRect()
  {
    super("rect");
  }

  public SVGRect at(double x, double y)
  {
    writeProperty("x", x);
    return writeProperty("y", y);
  }

  public SVGRect size(double x, double y)
  {
    writeProperty("width", x);
    return writeProperty("height", y);
  }
}
