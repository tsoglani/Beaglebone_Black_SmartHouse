package ch.eitchnet.beaglebone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Main object to give access to GPIO ports on a Linux kernel
 * </p>
 * 
 * <p>
 * The {@link GpioBridge} is a singleton. Features include retrieving Pins, writing and reading values, as well as
 * registering observers for changes to input pins
 * </p>
 * 
 * <p>
 * {@link Gpio} objects are cached and their {@link Signal} is set by the {@link GpioBridge} accordingly
 * </p>
 * 
 * @author Robert von Burg &lt;eitch@eitchnet.ch&gt;
 */
public class GpioBridge {

	private static final String GPIO_PATH = "/sys/class/gpio/";

	private Map<Pin, Gpio> cache;
	private Map<Gpio, List<GpioSignalListener>> listeners;
	private Thread thread;
	private volatile boolean run;

	private static final GpioBridge instance;

	static {
		instance = new GpioBridge();
	}

	/**
	 * @return the instance of the {@link GpioBridge}
	 */
	public static GpioBridge getInstance() {
		return instance;
	}

	/**
	 * Private constructor for singleton construction
	 */
	private GpioBridge() {
		this.cache = new HashMap<>();
		this.listeners = Collections.synchronizedMap(new HashMap<>());
	}

	/**
	 * Returns the kernel file path to the value of the {@link Gpio}
	 * 
	 * @param gpio
	 *            the {@link Gpio} for which the path is to be returned
	 * 
	 * @return the Path to the {@link Gpio}'s value
	 */
	private File getGpioValuePath(Gpio gpio) {
		return new File(GPIO_PATH, gpio.getKernelName() + "/value");
	}

	/**
	 * Returns the kernel file path to the direction of the {@link Gpio}
	 * 
	 * @param gpio
	 *            the {@link Gpio} for which the path is to be returned
	 * 
	 * @return the Path to the {@link Gpio}'s direction
	 */
	private File getGpioDirectionPath(Gpio gpio) {
		return new File(GPIO_PATH, gpio.getKernelName() + "/direction");
	}

	/**
	 * <p>
	 * Public API method to write the given {@link Signal} on the given {@link Gpio}'s pin.
	 * </p>
	 * 
	 * @param gpio
	 *            the {@link Gpio} to which the {@link Signal} should be written
	 * @param signal
	 *            the {@link Signal} to write to the given {@link Gpio}
	 * 
	 * @throws GpioException
	 *             if the direction of the {@link Gpio} is not {@link Direction#OUT}, or if something goes wrong while
	 *             writing to the file
	 */
	public void writeValue(Gpio gpio, Signal signal) throws GpioException {

		synchronized (gpio) {
			if (gpio.getDirection() != Direction.OUT)
				throw new GpioException("For writing the direction must be " + Direction.OUT);

			File file = getGpioValuePath(gpio);
			try (FileOutputStream out = new FileOutputStream(file)) {

				out.write(signal.getValueS().getBytes());
				out.flush();

				gpio.setSignal(signal);

			} catch (Exception e) {
				throw new GpioException("Failed to write GPIO " + gpio + " with signal " + signal, e);
			}

			System.out.println("Set GPIO " + gpio.getPin() + " signal to " + gpio.getSignal());
		}
	}

	/**
	 * <p>
	 * Public API method to read the current {@link Signal} on the given {@link Gpio}'s pin.
	 * </p>
	 * 
	 * @param gpio
	 *            the {@link Gpio} for which the {@link Signal} should be read
	 * 
	 * @return The {@link Gpio}'s current signal
	 * 
	 * @throws GpioException
	 *             if the direction of the {@link Gpio} is not {@link Direction#IN}, or if something goes wrong while
	 *             reading from the file
	 */
	public Signal readValue(Gpio gpio) throws GpioException {

		synchronized (gpio) {

			if (gpio.getDirection() != Direction.IN)
				throw new GpioException("For reading the direction must be " + Direction.IN);

			File file = getGpioValuePath(gpio);
			try (BufferedReader fin = new BufferedReader(new FileReader(file))) {

				String valueS = fin.readLine();
				Signal signal = Signal.getSignal(valueS);
				if (!gpio.getSignal().equals(signal))
					gpio.setSignal(signal);

				return signal;

			} catch (Exception e) {
				throw new GpioException("Failed to read GPIO " + gpio, e);
			}
		}
	}

	/**
	 * Starts the {@link GpioBridge}'s signal observing {@link Thread}. If no observers are registered with the
	 * {@link #register(Gpio, GpioSignalListener)}-method, then this method needs not to be called.
	 */
	public void start() {

		this.run = true;
		this.thread = new Thread(() -> {
			while (this.run) {
				if (this.listeners.isEmpty()) {
					synchronized (this) {
						try {
							wait(1000l);
						} catch (InterruptedException e) {
							System.out.println("Was interrupted. Stopping thread.");
							this.run = false;
							break;
						}
					}
				} else {

					List<Gpio> changes = new ArrayList<>();

					synchronized (this.listeners) {
						for (Gpio gpio : this.listeners.keySet()) {
							try {
								synchronized (gpio) {
									Signal currentSignal = gpio.getSignal();
									Signal newSignal = readValue(gpio);
									if (currentSignal != newSignal)
										changes.add(gpio);
								}
							} catch (Exception e) {
								System.out.println("Failed to read GPIO " + gpio + " due to:");
								e.printStackTrace();
								this.run = false;
								break;
							}
						}
					}

					if (!changes.isEmpty())
						System.out.println("Found " + changes.size() + " GPIO changes.");

					for (Gpio gpio : changes) {
						synchronized (this.listeners) {
							List<GpioSignalListener> listeners = this.listeners.get(gpio);
							System.out.println("GPIO " + gpio.getPin() + " changed to " + gpio.getSignal()
									+ ". Notifying " + listeners.size() + " listeners.");

							for (GpioSignalListener listener : listeners) {
								try {
									listener.notify(gpio);
								} catch (Exception e) {
									System.out.println("Failed to update listener " + listener + " due to:");
									e.printStackTrace();
								}
							}
						}
					}

					try {
						Thread.sleep(200l);
					} catch (InterruptedException e) {
						System.out.println("Was interrupted. Stopping thread.");
						this.run = false;
						break;
					}
				}
			}
		}, "gpio_reader");
		this.thread.start();
		System.out.println("Started GPIO bridge.");
	}

	/**
	 * Stops observing any pins and stops the {@link Thread}
	 */
	public void stop() {
		this.run = false;
		this.thread.interrupt();
		try {
			this.thread.join(5000l);
		} catch (InterruptedException e) {
			System.out.println("Was interrupted while waiting for thread to stop?!");
		}
	}

	/**
	 * <p>
	 * Returns the {@link Gpio} for the given {@link Direction}.
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b> This method can not be called multiple times with different {@link Direction}s. The
	 * {@link GpioBridge} does not handle pins that are simultaneously input and output as this is not supported by the
	 * Linux kernel.
	 * </p>
	 * 
	 * @param pin
	 *            The {@link Pin} for which the {@link Gpio} in the given {@link Direction} is to be returned
	 * @param direction
	 *            the {@link Direction} for which this {@link Gpio} is to be returned
	 * 
	 * @return The {@link Gpio} with the configured {@link Direction}
	 * 
	 * @throws GpioException
	 *             If the given {@link Direction} does not match the kernel's configured direction, or if the file
	 *             permissions are not set so that the Java process can access the file (read access for input pin,
	 *             write access for output pin.
	 */
	public synchronized Gpio getGpio(Pin pin, Direction direction) throws GpioException {
		Gpio gpio = this.cache.get(pin);
		if (gpio == null) {

			gpio = new Gpio(pin, direction);

			// validate direction
			assertDirection(gpio);

			// validate file permissions
			validateFilePermissions(gpio);

			this.cache.put(pin, gpio);
			System.out.println("Initialized pin " + pin + " with direction " + direction + ".");
		}

		return gpio;
	}

	/**
	 * Validates the direction of the {@link Gpio} is the same as kernel's exported state
	 * 
	 * @param gpio
	 *            the {@link Gpio} been asserted for direction
	 * 
	 * @throws GpioException
	 *             if the assertion fails
	 */
	private void assertDirection(Gpio gpio) throws GpioException {
		File file = getGpioDirectionPath(gpio);
		Pin pin = gpio.getPin();
		try (BufferedReader fin = new BufferedReader(new FileReader(file))) {

			String directionS = fin.readLine();
			Direction dir = Direction.getDirection(directionS);
			if (dir != gpio.getDirection())
				throw new GpioException("Actual direction of GPIO " + pin + " is " + dir + " not " + directionS);

		} catch (FileNotFoundException e) {
			throw new GpioException("GPIO " + pin + " does not exist, was the pin exported to user space?", e);
		} catch (IOException e) {
			throw new GpioException("Failed to open GPIO " + pin, e);
		}
	}

	/**
	 * Validates the file permissions of the {@link Gpio} is correct for the {@link Gpio}'s {@link Direction}:
	 * <ul>
	 * <li>read access for input pin</li>
	 * <li>write access for output pin</li>
	 * </ul>
	 * 
	 * @param gpio
	 *            the {@link Gpio} been asserted for direction
	 * 
	 * @throws GpioException
	 *             if the assertion fails
	 */
	private void validateFilePermissions(Gpio gpio) throws GpioException {
		File gpioValuePath = getGpioValuePath(gpio);
		Direction direction = gpio.getDirection();
		if (direction == Direction.IN) {
			if (!gpioValuePath.canRead())
				throw new GpioException("GPIO " + gpio + " has direction " + direction
						+ " and is not readable. Are the file permissions ok?");

		} else if (direction == Direction.OUT) {
			if (!gpioValuePath.canWrite())
				throw new GpioException("GPIO " + gpio + " has direction " + direction
						+ " and is not writable. Are the file permissions ok?");
		} else {
			throw new RuntimeException("Unhandled Direction " + direction);
		}
	}

	/**
	 * Registers the given {@link GpioSignalListener} for changes to {@link Signal}s on the given {@link Gpio}
	 * 
	 * @param gpio
	 *            the {@link Gpio} being observed
	 * @param listener
	 *            the {@link GpioSignalListener} to be notified on changes on the {@link Gpio}'s {@link Signal}
	 * 
	 * @throws GpioException
	 *             if the {@link Direction} of the {@link Gpio} is not {@link Direction#IN}
	 */
	public void register(Gpio gpio, GpioSignalListener listener) throws GpioException {

		if (gpio.getDirection() != Direction.IN)
			throw new GpioException("For reading the direction must be " + Direction.IN);

		synchronized (this.listeners) {
			List<GpioSignalListener> listeners = this.listeners.get(gpio);
			if (listeners == null) {
				listeners = new ArrayList<>();
				this.listeners.put(gpio, listeners);
			}

			listeners.add(listener);
		}

		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 * Unregisters a {@link GpioSignalListener} from changes to the given {@link Gpio}
	 * 
	 * @param gpio
	 *            the {@link Gpio} for which the listener is to be removed
	 * @param listener
	 *            the {@link GpioSignalListener} to be removed from changes to the given {@link Gpio}
	 * 
	 * @return true if the listener was unregistered, false if not
	 */
	public boolean unregister(GpioBridgeTest gpio, GpioSignalListener listener) {
		synchronized (this.listeners) {
			List<GpioSignalListener> listeners = this.listeners.get(gpio);
			if (listeners == null) {
				return false;
			}

			boolean removed = listeners.remove(listener);

			if (listeners.isEmpty())
				this.listeners.remove(gpio);

			return removed;
		}
	}
}