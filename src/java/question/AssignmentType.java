package question;

import java.util.Optional;

public enum AssignmentType {
	SINUSOIDAL_GRAPH("Analyzing sinusoidal graphs"),
	RIGHT_TRIANGLE_RATIOS("Special Right Triangle Problems"),
	RIGHT_TRIANGLE("Right Triangle Problems");

	final String readableName;

	AssignmentType() {
		readableName = this.name();
	}

	AssignmentType(String name) {
		readableName = name;
	}

	public static Optional<AssignmentType> tryParse(String from) {
		try {
			return Optional.of(valueOf(from));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
}
