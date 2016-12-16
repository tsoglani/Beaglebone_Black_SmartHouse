package ch.eitchnet.beaglebone;

/**
 * <p>
 * Defines a {@link Gpio} using the combination of {@link Pin}, {@link Direction} and {@link Signal}
 * </p>
 * 
 * <p>
 * {@link Gpio} are instantiated by the {@link GpioBridge} by calling {@link GpioBridge#getGpio(Pin, Direction)}
 * </p>
 * 
 * <p>
 * The {@link Gpio}'s {@link Signal} is always updated by the {@link GpioBridge} when a new {@link Signal} is read or
 * written.
 * </p>
 * 
 * @author Robert von Burg &lt;eitch@eitchnet.ch&gt;
 */
public class Gpio {

	private final Pin pin;
	private final String kernelName;
	private final Direction direction;
	private Signal signal;
	private String label;

	/**
	 * Constructs a new {@link Gpio}
	 * 
	 * @param pin
	 *            the {@link Pin}
	 * @param direction
	 *            the {@link Direction}
	 */
	Gpio(Pin pin, Direction direction) {
		this.pin = pin;
		this.direction = direction;
		this.kernelName = "gpio" + pin.getGpioNr();
		this.label = kernelName;
		this.signal = Signal.LOW;
	}

	/**
	 * @return the reference to this {@link Gpio}'s {@link Pin}
	 */
	public Pin getPin() {
		return this.pin;
	}

	/**
	 * @return the kernel name e.g. "gpio60"
	 */
	public String getKernelName() {
		return this.kernelName;
	}

	/**
	 * Allows the user to set a label for this pin, e.g. "Green Button", "Red Led"
	 * 
	 * @param label
	 *            the label to set, e.g. "Green Button", "Red Led"
	 * 
	 * @return this {@link Gpio} for call chaining
	 */
	public Gpio setLabel(String label) {
		this.label = label;
		return this;
	}

	/**
	 * @return the user configurable label of this pin, e.g. "Green Button", "Red Led"
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @return the {@link Gpio}'s {@link Direction}
	 */
	public Direction getDirection() {
		return this.direction;
	}

	/**
	 * @return the {@link Gpio}'s current {@link Signal}
	 */
	public Signal getSignal() {
		return this.signal;
	}

	/**
	 * <p>
	 * THIS METHOD IS ONLY CALLED BY THE {@link GpioBridge}
	 * </p>
	 * 
	 * <p>
	 * Set the {@link Gpio}'s current {@link Signal}
	 * </p>
	 * 
	 * @param signal
	 *            the new {@link Signal} to set
	 * 
	 * @return this {@link Gpio} instance for call chaining
	 */
	Gpio setSignal(Signal signal) {
		this.signal = signal;
		return this;
	}

	/**
	 * @see Pin#toString()
	 */
	@Override
	public String toString() {
		return this.pin.toString();
	}
}