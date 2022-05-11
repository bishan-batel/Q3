package question.generators;

import question.Problem;
import question.QuestionData;

@QuestionData(name = "Lascaud")
public class LascaudProblem extends Problem {
	@Override
	protected void refresh() {
	}

	@Override
	public boolean isCorrect(String test) {
		return test.equalsIgnoreCase("lascaud");
	}

	@Override
	public String getQuestionText() {
		return "Who is the coolest guy?";
	}

	@Override
	public Answer getAnswerPrompt() {
		return Answer.withFreeTextInput("{input}");
	}
}
