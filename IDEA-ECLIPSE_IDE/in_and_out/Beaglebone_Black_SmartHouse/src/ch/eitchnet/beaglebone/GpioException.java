package ch.eitchnet.beaglebone;

/**
 * General {@link Exception} for exceptional situations while using the {@link GpioBridge}
 * 
 * @author Robert von Burg &lt;eitch@eitchnet.ch&gt;
 */
public class GpioException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a {@link GpioException} with message and cause
	 * 
	 * @param message
	 *            the exception message
	 * @param cause
	 *            the casue of the exception
	 */
	public GpioException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct a {@link GpioException} with message
	 * 
	 * @param message
	 *            the exception message
	 */
	public GpioException(String message) {
		super(message);
	}
}
