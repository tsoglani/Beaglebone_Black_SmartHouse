package ch.eitchnet.beaglebone;

/**
 * <p>
 * Defines the direction of a {@link Gpio}
 * </p>
 * 
 * <p>
 * A {@link Gpio} can be either an input pin, or an output pin. This is defined by this enum
 * </p>
 * 
 * @author Robert von Burg &lt;eitch@eitchnet.ch&gt;
 */
public enum Direction {
	IN("in"), OUT("out");

	private String direction;

	private Direction(String direction) {
		this.direction = direction;
	}

	/**
	 * @return the direction
	 */
	public String getDirection() {
		return this.direction;
	}

	/**
	 * Parses the direction enum from the given value
	 * 
	 * @param directionS
	 *            the direction string to parse
	 * 
	 * @return the direction parsed from the given string
	 */
	public static Direction getDirection(String directionS) {
		if (directionS.equals(IN.direction))
			return IN;
		else if (directionS.equals(OUT.direction))
			return OUT;
		throw new IllegalArgumentException("No direction for value " + directionS);
	}
}