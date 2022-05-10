package question;

import svg.SVGBuilder;
import svg.SVGCircle;
import svg.SVGStyle;
import svg.SVGText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author bishan
 */
public class TestProblem extends Problem {

	@Override
	public void refresh() {
		SVGBuilder graph = new SVGBuilder().bg("red");
		graph.draw(new SVGCircle()
						.pos(50, 50)
						.size(50)
						.style(new SVGStyle().fill("white"))
		);

		graph.draw(new SVGText("test graph")
						.at(10, 50)
						.style(new SVGStyle().css("font-weight", "bold"))
		);
		addGraph(graph);
		exampleCorrectAnswer = "Vinny";
	}

	@Override
	public boolean isCorrect(String test) {
		return test.toLowerCase().matches("\\s*vinny\\s*");
	}

	@Override
	public String getQuestionText() {
		return "My cousin's name is vinny";
	}

	@Override
	public Answer getAnswerPrompt() {
		return Answer.withFreeTextInput("What is my <b>cousins</b> name? <br> {input}");
	}

	public static void main(String[] args) throws IOException {
		Problem problem = new TestProblem();
		problem.refresh();

		File file = new File("test.html");
		file.createNewFile();

		try (FileWriter fw = new FileWriter("test.html")) {
			fw.write(problem.getQuestionText());
			for (String s : problem.getGraphsHTML()) {
				fw.write(s);
			}
			fw.write(problem.getAnswerPrompt().getMainPrompt());
		}
	}
}
