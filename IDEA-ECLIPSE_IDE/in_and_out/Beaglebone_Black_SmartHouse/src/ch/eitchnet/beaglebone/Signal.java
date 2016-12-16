package ch.eitchnet.beaglebone;

/**
 * <p>
 * Defines the {@link Signal} which a {@link Gpio} can have
 * </p>
 * 
 * <p>
 * A pin's signal is its current value. Depending on the {@link Gpio}'s {@link Direction}, the signal is an input value,
 * or an output value.
 * </p>
 * 
 * <p>
 * A signal of {@link #LOW} means that the value is 0, and a value of {@link #HIGH} means the value is 1
 * </p>
 * 
 * @author Robert von Burg &lt;eitch@eitchnet.ch&gt;
 */
public enum Signal {
	LOW(0, "0", false, "low"), HIGH(1, "1", true, "high");

	private int value;
	private String valueS;
	private boolean high;
	private String signal;

	private Signal(int value, String valueS, boolean high, String signal) {
		this.value = value;
		this.valueS = valueS;
		this.high = high;
		this.signal = signal;
	}

	/**
	 * @return the value which is either 0 or 1
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * @return the value as a string which is either 0 or 1
	 */
	public String getValueS() {
		return this.valueS;
	}

	/**
	 * @return true for a high signal, i.e. value of 1. Return false for a low value, i.e. a value of 0
	 */
	public boolean isHigh() {
		return this.high;
	}

	/**
	 * @return true for a low signal, i.e. value of 0. Return false for a high value, i.e. a value of 1
	 */
	public String getSignal() {
		return this.signal;
	}

	/**
	 * @return the opposite of the current {@link Signal}
	 */
	public Signal getOpposite() {
		if (this.high)
			return LOW;
		return HIGH;
	}

	/**
	 * Returns the {@link Signal} enum for the given value
	 * 
	 * @param value
	 *            the value for which to return the {@link Signal}
	 * 
	 * @return the {@link Signal} for the given value
	 */
	public static Signal getSignal(int value) {
		if (value == 0)
			return LOW;
		else if (value == 1)
			return HIGH;
		throw new IllegalArgumentException("No signal for value " + value);
	}

	/**
	 * Returns the {@link Signal} enum for the given string value
	 * 
	 * @param valueS
	 *            the value for which to return the {@link Signal}
	 * 
	 * @return the {@link Signal} for the given string value
	 */
	public static Signal getSignal(String valueS) {
		if (valueS.equals(LOW.valueS))
			return LOW;
		else if (valueS.equals(HIGH.valueS))
			return HIGH;
		throw new IllegalArgumentException("No signal for value " + valueS);
	}
}