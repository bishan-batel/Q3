package question;

import svg.SVGBuilder;
import utils.Guard;

import java.util.ArrayList;
import java.util.List;

public abstract class Problem {
	private final List<SVGBuilder> graphs = new ArrayList<>();
	protected String exampleCorrectAnswer;

	protected void addGraph(SVGBuilder graph) {
		Guard.forNull(graph);
		graphs.add(graph);
	}


	/**
	 * Generates requirements
	 */
	protected abstract void refresh();

	public final void generate() {
		graphs.clear();
		exampleCorrectAnswer = null;
		refresh();
	}


	public abstract boolean isCorrect(String test);

	public final String getCorrectAnswer() {
		return exampleCorrectAnswer;
	}

	/**
	 * Will return a string used for the beginning part of the question
	 */
	public abstract String getQuestionText();

	/**
	 * Returns answer prompt for the problem
	 */
	public abstract Answer getAnswerPrompt();

	/**
	 * Returns html for all graphs required for the display of this problem
	 */
	public final String[] getGraphsHTML() {
		return graphs.stream().map(SVGBuilder::build).toArray(String[]::new);
	}

	public final int getGraphCount() {
		return graphs.size();
	}

	/**
	 * Used to store information of how the user is supposed to answer to a question
	 * <p>
	 * Missing Java17 with record types :(
	 */
	public static final class Answer {
		private final String mainPrompt;
		private final String[] options;

		private Answer(String question, String[] options) {
			this.mainPrompt = question;
			this.options = options;
		}

		private Answer(String question) {
			this(question, null);
		}

		// getters and setters
		public String getMainPrompt() {
			return mainPrompt;
		}

		public String[] getOptions() {
			Guard.forNull((Object) options);
			return options;
		}

		/**
		 * Returns if  to be answered with a free user text box
		 */
		public boolean isFreeText() {
			return options == null;
		}

		/**
		 * Returns if to be answered with a list of options
		 */
		public boolean isOptions() {
			return !isFreeText();
		}

		// static utils
		public static Answer withFreeTextInput(String question) {
			return new Answer(question);
		}

		public static Answer withOptions(String question, String... options) {
			return new Answer(question, options);
		}
	}

}
