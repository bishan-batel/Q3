package question;

import question.generators.PythagThereomProblem;
import question.generators.SinusoidalGraphProblem;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author bishan
 */
public enum ProblemType {
	SINUSOIDAL_GRAPH("Analyzing sinusoidal graphs", SinusoidalGraphProblem::new),
	PYTHAG_THEREOM("Pythagorous's Theorom", PythagThereomProblem::new),
//	RIGHT_TRIANGLE_RATIOS("Special Right Triangle Problems", TestProblem::new),
//	RIGHT_TRIANGLE("Right Triangle Problems", TestProblem::new),

	DEBUG_TEST("Test Problem", TestProblem::new);
	// Enum Instance
	private final String readableName;
	private final ProblemGenerator generator;

	ProblemType(String name, ProblemGenerator generator) {
		this.readableName = name;
		this.generator = generator;
	}

	public Problem generate() {
		return generator.createProblem();
	}

	@Override
	public String toString() {
		return readableName;
	}

	// Static helper methods

	/**
	 * Attempts
	 */
	public static Optional<ProblemType> tryParse(String from) {
		if (from == null) return Optional.empty();
		try {
			return Optional.of(valueOf(from));
		} catch (IllegalArgumentException e) {
			return Arrays.stream(values())
							.filter(t -> t.toString().equalsIgnoreCase(from))
							.findFirst();
		}
	}

	@FunctionalInterface
	private interface ProblemGenerator {
		Problem createProblem();
	}
}
