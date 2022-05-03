/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package svg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;

import static java.lang.String.format;
import static svg.SVGColor.rgb;

@SuppressWarnings("unused")
public class SVGBuilder
{

	private final int width, height;
	private final StringBuilder builder;

	public SVGBuilder()
	{
		this(100, 100);
	}

	public SVGBuilder(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.builder = new StringBuilder();
		builder.append(format("<svg width=\"%d\" height=\"%d\">", width, height));
	}

	public String build()
	{
		return builder.toString() + "\n</svg>";
	}

	public SVGBuilder bg(String col)
	{
		return this.draw(new SVGRect()
			.at(0, 0)
			.size(width, height)
			.style(new SVGStyle()
				.fill(col)
			));
	}

	public SVGBuilder draw(SVGObject<?> obj)
	{
		String element = obj.toString();
		builder.append("\n");
		builder.append(element);
		return this;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public static void main(String[] args) throws IOException
	{
		String svg = new SVGBuilder(100, 100)
			.bg(rgb(0, 0, 0))
			.draw(new SVGRect()
				.at(50, 50)
				.size(50, 50)
				.style(new SVGStyle()
					.fill(rgb(255, 0, 0))
					.stroke(rgb(100, 0, 0))
					.css("border-radius", "20px")
				)
			)
			.build();

		File file = new File("test.html");
		if (!file.createNewFile()) throw new FileSystemException("bruh");

		try (FileWriter fw = new FileWriter("test.html"))
		{
			fw.write("<!DOCTYPE html><html><body>");
			fw.write(svg);
		}

	}
}
