package question.generators;

import math.Triangle;
import math.Vector2;
import question.Problem;
import question.QuestionData;
import svg.SVGBuilder;
import svg.SVGPath;
import svg.SVGStyle;
import svg.SVGText;
import utils.WebUtils;

@QuestionData(name = "Pythag Problem")
public class PythagThereomProblem extends Problem {
	private enum Variable {
		A, B, C
	}

	private double correct;

	@Override
	protected void refresh() {
		SVGBuilder graph = new SVGBuilder().bg("white");

		Triangle tri = new Triangle();
		tri.pointA = new Vector2(20, 80);
		tri.pointB = tri.pointA.add(new Vector2(15 + Math.random() * 60, 0)).round();
		tri.pointC = tri.pointA.sub(new Vector2(0, 15 + Math.random() * 60)).round();


		Variable missing = Variable.values()[(int) (Variable.values().length * Math.random())];

		double sideA = tri.pointB.distanceTo(tri.pointC),
						sideB = tri.pointC.distanceTo(tri.pointA),
						sideC = tri.pointA.distanceTo(tri.pointB);

		graph.draw(new SVGPath()
						.move(tri.pointA.x, tri.pointA.y)
						.line(tri.pointB.x, tri.pointB.y)
						.line(tri.pointC.x, tri.pointC.y)
						.close()
						.style(new SVGStyle()
										.fill("indianred")
						)
		);

		SVGStyle textStyle = new SVGStyle()
						.fontSize("5px")
						.css("text-align", "center");


		Vector2 point = tri.pointB.midpoint(tri.pointC);
		SVGText txtA = new SVGText()
						.write(WebUtils.truncate(sideA, 2) + "")
						.at(point.x, point.y)
						.style(textStyle);


		point = tri.pointA.midpoint(tri.pointB);
		SVGText txtC = new SVGText()
						.write(sideC + "")
						.at(point.x, point.y)
						.style(textStyle);

		point = tri.pointA.midpoint(tri.pointC);
		SVGText txtB = new SVGText()
						.write(sideB + "")
						.at(point.x, point.y)
						.style(textStyle);

		if (missing != Variable.A) graph.draw(txtA);
		if (missing != Variable.B) graph.draw(txtB);
		if (missing != Variable.C) graph.draw(txtC);

		if (missing == Variable.A) correct = sideA;
		if (missing == Variable.B) correct = sideB;
		if (missing == Variable.C) correct = sideC;

		addGraph(graph);
	}

	@Override
	public boolean isCorrect(String test) {
		try {
			double d = Double.parseDouble(test);
			return Math.abs(correct - d) < .05d;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	@Override
	public String getQuestionText() {
		return "What is the missing side?";
	}

	@Override
	public Answer getAnswerPrompt() {
		return Answer.withFreeTextInput("Answer to the nearest hundredth <br> {input}");
	}
}
