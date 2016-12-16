package ch.eitchnet.beaglebone;

import io.silverspoon.bulldog.beagleboneblack.BeagleBonePin;

/**
 * <p>
 * This enum defines all the {@link Gpio} pins of the BeagleBoneBlack
 * </p>
 * 
 * <p>
 * The BealgeBoneBlack has two banks of pins, of which not all pins can be used as a GPIO (general purpose
 * input/outpunt) pin.
 * </p>
 * 
 * @author Robert von Burg &lt;eitch@eitchnet.ch&gt;
 */
public enum Pin {

	P8_03("P8.03", 1, 6),
	P8_04("P8.04", 1, 7),
	P8_05("P8.05", 1, 2),
	P8_06("P8.06", 1, 3),
	P8_07("P8.07", 2, 2),
	P8_08("P8.08", 2, 3),
	P8_09("P8.09", 2, 5),
	P8_10("P8.10", 2, 4),
	P8_11("P8.11", 1, 13),
	P8_12("P8.12", 1, 12),
	P8_13("P8.13", 0, 23),
	P8_14("P8.14", 0, 26),
	P8_15("P8.15", 1, 15),
	P8_16("P8.16", 1, 14),
	P8_17("P8.17", 0, 27),
	P8_18("P8.18", 2, 1),
	P8_19("P8.19", 0, 22),
	P8_20("P8.20", 1, 31),
	P8_21("P8.21", 1, 30),
	P8_22("P8.22", 1, 5),
	P8_23("P8.23", 1, 4),
	P8_24("P8.24", 1, 1),
	P8_25("P8.25", 1, 0),
	P8_26("P8.26", 1, 29),
	P8_27("P8.27", 2, 22),
	P8_28("P8.28", 2, 24),
	P8_29("P8.29", 2, 23),
	P8_30("P8.30", 2, 25),
	P8_31("P8.31", 0, 10),
	P8_32("P8.32", 0, 11),
	P8_33("P8.33", 0, 9),
	P8_34("P8.34", 2, 17),
	P8_35("P8.35", 0, 8),
	P8_36("P8.36", 2, 16),
	P8_37("P8.37", 2, 14),
	P8_38("P8.38", 2, 15),
	P8_38B("P8.38", 2, 1),//

	P8_39("P8.39", 2, 12),
	P8_40("P8.40", 2, 13),
	P8_40B("P8.40", 2, 14),//

	P8_41("P8.41", 2, 10),
	P8_42("P8.42", 2, 11),
	P8_43("P8.43", 2, 8),
	P8_44("P8.44", 2, 9),
	P8_45("P8.45", 2, 6),
	P8_46("P8.46", 2, 7),
	P8_46B("P8.46", 2,27),//

	P9_11("P9.11", 0, 30),
	P9_12("P9.12", 1, 28),
	P9_13("P9.13", 0, 31),
	P9_14("P9.14", 1, 18),
	P9_15("P9.15", 1, 16),
	P9_16("P9.16", 1, 19),
	P9_17("P9.17", 0, 5),
	P9_18("P9.18", 0, 4),
	P9_19("P9.19", 0, 13),
	P9_19B("P9.19", 0, 15),//

	P9_20("P9.20", 0, 12),
	P9_20B("P9.20", 0, 14),//

	P9_21("P9.21", 0, 3),
	P9_22("P9.22", 0, 2),
	P9_23("P9.23", 1, 17),
	P9_24("P9.24", 0, 15),
	P9_24B("P9.24", 0, 12),//


	P9_25("P9.25", 3, 21),
	P9_26("P9.26", 0, 14),
	P9_26B("P9.26", 0, 13),//

	P9_27("P9.27", 3, 19),
	P9_28("P9.28", 3, 17),
	P9_29("P9.29", 3, 15),
	P9_30("P9.30", 3, 16),
	P9_31("P9.31", 3, 14),
	P9_41A("P9.41A", 0, 20),
	P9_41B("P9.41B", 3, 20),
	P9_42A("P9.42A", 0, 7),
	P9_42B("P9.42B", 3, 18),
	P9_41("P9.41", 0, 20),//
	P9_42("P9.42", 0, 7),//

	P9_33("P9.33", 2, 7),
	P9_35("P9.35", 2, 9),
	P9_36("P9.36", 2, 8),
	P9_37("P9.37", 2,5),
	P9_38("P9.38", 2, 6),
	P9_39("P9_39", 2, 3),
	P9_40("P9.40", 2, 4),

	J1_1("J1.1", 0,0),//
	J1_2("J1.2", 0,0),//
	J1_3("J1.3", 0,0),//
	J1_4("J1.4", 0,0),//
	J1_5("J1.5", 0,0),//
	J1_6("J1.6", 0,0)

	;

	private String label;
	private int chip;
	private int pin;

	private Pin(String label, int chip, int pin) {
		this.label = label;
		this.chip = chip;
		this.pin = pin;
	}


	/**
	 * @return the user label of this pin, e.g. "P8.03"
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @return the GPIO chip on which this Pin is configured on the BeagleBoneBlack's CPU
	 */
	public int getChip() {
		return this.chip;
	}

	/**
	 * @return The kernel pin number. <b>Note:</b> this is not the pin number on the pin header, neither the GPIO number
	 *         used to export the pin to userspace
	 */
	public int getPin() {
		return this.pin;
	}

	/**
	 * @return The GPIO number with which the pin is exported to userspace
	 */
	public int getGpioNr() {
		return this.chip * 32 + this.pin;
	}

	/**
	 * Returns the configured label e.g. "P8.03"
	 */
	@Override
	public String toString() {
		return this.label;
	}
}
