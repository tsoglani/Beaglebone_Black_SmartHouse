package ch.eitchnet.beaglebone;

/**
 * Bridge test class
 * 
 * @author Robert von Burg &lt;eitch@eitchnet.ch&gt;
 */
public class GpioBridgeTest {

	private static Gpio redBtn;
	private static Gpio blueBtn;
	private static Gpio greenBtn;

	private static Gpio green0;

	private static Gpio yellow0;
	private static Gpio yellow1;
	private static Gpio yellow2;

	private static Gpio green1;
	private static Gpio red0;

	private static Thread workThread;
	private static volatile boolean doWork;
	private static volatile Gpio workBtnPressed;

	public static void main(String[] args) throws Exception {

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		GpioBridge gpioBridge = GpioBridge.getInstance();

		System.out.println("Preparing pins...");
		greenBtn = gpioBridge.getGpio(Pin.P8_07, Direction.IN).setLabel("Green");
		blueBtn = gpioBridge.getGpio(Pin.P8_08, Direction.IN).setLabel("Blue");
		redBtn = gpioBridge.getGpio(Pin.P8_09, Direction.IN).setLabel("Red");

		green0 = gpioBridge.getGpio(Pin.P8_10, Direction.OUT).setLabel("green0");
		yellow0 = gpioBridge.getGpio(Pin.P8_11, Direction.OUT).setLabel("yellow0");
		yellow1 = gpioBridge.getGpio(Pin.P8_12, Direction.OUT).setLabel("yellow1");
		yellow2 = gpioBridge.getGpio(Pin.P8_14, Direction.OUT).setLabel("yellow2");
		green1 = gpioBridge.getGpio(Pin.P8_15, Direction.OUT).setLabel("green1");
		red0 = gpioBridge.getGpio(Pin.P8_16, Direction.OUT).setLabel("red0");
		System.out.println("Prepared pins.");

		System.out.println("Registering listeners...");
		GpioSignalListener btnListener = (g) -> {
			if (g.getSignal().isHigh())
				System.out.println("User pressed " + g.getLabel() + " button.");
			else
				System.out.println("User released " + g.getLabel() + " button.");
		};
		gpioBridge.register(redBtn, btnListener);
		gpioBridge.register(blueBtn, btnListener);
		gpioBridge.register(greenBtn, btnListener);

		gpioBridge.register(greenBtn, g -> startWork(gpioBridge));
		gpioBridge.register(blueBtn, g -> stopWork(blueBtn, gpioBridge));
		gpioBridge.register(redBtn, g -> stopWork(redBtn, gpioBridge));
		System.out.println("Registered listeners.");

		resetLeds(gpioBridge);

		gpioBridge.start();
	}

	private static void resetLeds(GpioBridge gpioBridge) throws GpioException {
		System.out.println("Resetting leds...");
		gpioBridge.writeValue(green0, Signal.LOW);
		gpioBridge.writeValue(yellow0, Signal.LOW);
		gpioBridge.writeValue(yellow1, Signal.LOW);
		gpioBridge.writeValue(yellow2, Signal.LOW);
		gpioBridge.writeValue(green1, Signal.LOW);
		gpioBridge.writeValue(red0, Signal.LOW);
	}

	private static void startWork(GpioBridge gpioBridge) throws GpioException {

		if (doWork) {
			System.out.println("Work already running!");
			return;
		}

		System.out.println("Starting work...");
		resetLeds(gpioBridge);

		doWork = true;

		workThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					gpioBridge.writeValue(green0, Signal.HIGH);

					while (doWork) {
						gpioBridge.writeValue(yellow2, Signal.LOW);
						gpioBridge.writeValue(yellow0, Signal.HIGH);
						Thread.sleep(250L);
						if (!doWork)
							break;
						gpioBridge.writeValue(yellow0, Signal.LOW);
						gpioBridge.writeValue(yellow1, Signal.HIGH);
						Thread.sleep(250L);
						if (!doWork)
							break;
						gpioBridge.writeValue(yellow1, Signal.LOW);
						gpioBridge.writeValue(yellow2, Signal.HIGH);
						Thread.sleep(250L);
					}

					gpioBridge.writeValue(green0, Signal.LOW);
					gpioBridge.writeValue(yellow0, Signal.LOW);
					gpioBridge.writeValue(yellow1, Signal.LOW);
					gpioBridge.writeValue(yellow2, Signal.LOW);

					if (workBtnPressed == null) {
						System.out.println("Uh-oh, no workBtnPressed set! Something is bad!");
						gpioBridge.writeValue(green1, Signal.HIGH);
						gpioBridge.writeValue(red0, Signal.HIGH);
					} else if (workBtnPressed == blueBtn) {
						System.out.println("Work completed successfully!");
						gpioBridge.writeValue(green1, Signal.HIGH);
						gpioBridge.writeValue(red0, Signal.LOW);
					} else {
						System.out.println("Work failed!");
						gpioBridge.writeValue(red0, Signal.HIGH);
						gpioBridge.writeValue(green1, Signal.LOW);
					}

				} catch (InterruptedException e) {
					throw new RuntimeException("Failed to do work.", e);
				} catch (GpioException e) {
					throw new RuntimeException("Failed to work GPIOs", e);
				}
			}
		}, "work");
		workThread.start();
	}

	private static void stopWork(Gpio btnPressed, GpioBridge gpioBridge) {
		workBtnPressed = btnPressed;
		doWork = false;
		try {
			workThread.join(2000l);
		} catch (InterruptedException e) {
			throw new RuntimeException("Was interrupted while stopping work!", e);
		}
	}
}
