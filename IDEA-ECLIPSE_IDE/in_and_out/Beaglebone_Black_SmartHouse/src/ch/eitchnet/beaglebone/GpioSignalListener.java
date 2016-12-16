package ch.eitchnet.beaglebone;

/**
 * <p>
 * Interface to define a listener for changes to a {@link Gpio}'s {@link Signal} when the {@link Direction} of the
 * {@link Gpio} is {@link Direction#IN}
 * </p>
 * 
 * <p>
 * Register the {@link GpioSignalListener} calling {@link GpioBridge#register(Gpio, GpioSignalListener)}
 * </p>
 * 
 * @author Robert von Burg &lt;eitch@eitchnet.ch&gt;
 */
public interface GpioSignalListener {

	/**
	 * Notifies the {@link GpioSignalListener} when a change to the {@link Gpio} is detected
	 * 
	 * @param gpio
	 *            the {@link Gpio} for which the {@link Signal} changed. The actual signal is to be retrieved by calling
	 *            {@link Gpio#getSignal()}
	 * 
	 * @throws Exception
	 *             if something goes wrong while handling the signal change
	 */
	public void notify(Gpio gpio) throws Exception;
}