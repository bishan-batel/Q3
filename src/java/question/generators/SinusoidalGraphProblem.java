package question.generators;

import question.Problem;
import question.QuestionData;
import svg.*;
import utils.WebUtils;

import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@QuestionData(name = "Sin graph problems")
public class SinusoidalGraphProblem extends Problem {
	private WaveType waveType;
	private double decimalCorrect;

	private enum WaveType {
		SIN, COS
	}

	@Override
	public void refresh() {
		waveType = Math.random() > .5 ? WaveType.SIN : WaveType.COS;

		Function<Double, Double> oscilattor = waveType == WaveType.COS ? Math::cos : Math::sin;
		double phaseOffset = (int) (Math.random() * 4) / 2f;
		decimalCorrect = phaseOffset;

		final int STEPS = 6;
		SVGBuilder graph = new SVGBuilder().bg("white");
		SVGStyle style = new SVGStyle().stroke("gray");
		for (double i = 0; i <= 100; i += 100d / STEPS) {
			graph.draw(new SVGLine()
							.from(i, 0)
							.to(i, 100)
							.style(style)
			).draw(new SVGLine()
							.from(0, i)
							.to(100, i)
							.style(style)
			);
		}

		style = new SVGStyle().stroke("blue").fill("transparent");
		SVGPath wave = new SVGPath().move(0, 0).style(style);

		for (double x = 0; x <= 100; x++) {
			double y = oscilattor.apply(Math.PI * -phaseOffset + Math.PI * 2 * (x + 50) * (STEPS / 2) / 100) * 100f / STEPS;
			wave.line(x, 50 - y);
		}
		graph.draw(wave);

		style = new SVGStyle()
						.stroke("green")
						.fontSize("8px")
						.fontWeight("light")
						.css("text-align", "center");

		for (int i = 0; i <= STEPS; i++) {
			graph.draw(new SVGText()
							.at(i * 100d / STEPS - 4, 53)
							.write(String.format("%dπ", (i - STEPS / 2)))
							.style(style)
			);

		}
		addGraph(graph);

		exampleCorrectAnswer = "" + decimalCorrect;
	}

	@Override
	public boolean isCorrect(String test) {
		try {
			double d = Double.parseDouble(test);
			return Math.abs(decimalCorrect - d) < .01d;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	@Override
	public String getQuestionText() {
		return String.format(
						"What is the phase offset of the %s wave?",
						waveType.toString().toLowerCase()
		);
	}

	@Override
	public Answer getAnswerPrompt() {
		return Answer.withFreeTextInput("Answer the <b>positive</b> coefficient for pi in decimal form<br>{input}pi");
	}

	public static void main(String[] args) {
	}
}
