package question;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bishan
 */
public final class ProblemType {
	private static final ProblemType[] values;
	private static final String PROBLEM_PACKAGE = "problem.generators";

	static {
		// legit the worst code i've ever written
		InputStream stream = ClassLoader.getSystemClassLoader()
						.getResourceAsStream(PROBLEM_PACKAGE.replaceAll("[.]", "/"));
		assert stream != null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		values = reader.lines()
						.filter(line -> line.endsWith(".class"))
						.map(line -> {
							try {
								return Class.forName(PROBLEM_PACKAGE + "." + line.substring(0, line.lastIndexOf('.')));
							} catch (ClassNotFoundException ignored) {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.filter(c -> c.isAssignableFrom(Problem.class))
						.map(c -> (Class<? extends Problem>) c)
						.map(c -> {
							QuestionData meta = c.getAnnotation(QuestionData.class);

							return new ProblemType(meta.name(), () -> {
								try {
									return c.getConstructor().newInstance();
								} catch (Exception e) {
									e.printStackTrace();
									return null;
								}
							});
						})
						.toArray(ProblemType[]::new);

	}


	// Enum Instance
	private final String readableName;
	private final ProblemGenerator generator;

	private ProblemType(String name, ProblemGenerator generator) {
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
		return Arrays.stream(values).filter(v -> v.readableName.equalsIgnoreCase(from)).findFirst();
	}

	@FunctionalInterface
	private interface ProblemGenerator {
		Problem createProblem();
	}

	public static ProblemType[] values() {
		return values;
	}
}
