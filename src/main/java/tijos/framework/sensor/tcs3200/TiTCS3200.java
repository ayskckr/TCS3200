package tijos.framework.sensor.tcs3200;

import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.eventcenter.ITiEvent;
import tijos.framework.eventcenter.ITiEventListener;
import tijos.framework.eventcenter.TiEventService;
import tijos.framework.eventcenter.TiEventType;
import tijos.framework.util.Delay;

/**
 * TCS3200/230 Color Sensor driver library for TiJOS
 * 
 *
 */

public class TiTCS3200 implements ITiEventListener {
	TiGPIO gpioPort;
	int S0, S1, S2, S3, OUT;

	int interval = 100;
	
	int counter = 0;

	int Ramount, Gamount, Bamount;
	int Rgena, Ggena, Bgena;

	public enum Filter {
		RGB_R, RGB_G, RGB_B, RGB_X
	};

	public enum Frequency {
		HIGH, MIDDLE, LOW, OFF
	};

	/**
	 * initialize with GPIO port and PINs
	 * @param gpioPort GPIO port 
	 * @param pinS0  S0 
	 * @param pinS1  S1
	 * @param pinS2  S2
	 * @param pinS3  S3
	 * @param pinOut counter output
	 */
	public TiTCS3200(TiGPIO gpioPort, int pinS0, int pinS1, int pinS2, int pinS3, int pinOut) {

		this.gpioPort = gpioPort;
		this.S1 = pinS1;
		this.S2 = pinS2;
		this.S3 = pinS3;
		this.OUT = pinOut;
	}

	/**
	 * Initialize GPIO event
	 * @throws IOException
	 */
	public void initialize() throws IOException {
		this.counter = 0;
		gpioPort.setEventParameters(this.OUT, TiGPIO.EVT_RISINGEDGE, 0);
		TiEventService.getInstance().addListener(this);

	}

	@Override
	public TiEventType getType() {
		return TiEventType.GPIO;
	}

	@Override
	public void onEvent(ITiEvent arg0) {
		this.counter++;

	}

	/**
	 * Select color channel 
	 * @param f - R G B 
	 * @throws IOException
	 */
	public void setFilter(Filter f) throws IOException {

		switch (f) {
		case RGB_R:
			this.gpioPort.writePin(this.S2, 0);
			this.gpioPort.writePin(this.S3, 0);
			break;
		case RGB_G:
			this.gpioPort.writePin(this.S2, 1);
			this.gpioPort.writePin(this.S3, 1);
			break;
		case RGB_B:
			this.gpioPort.writePin(this.S2, 0);
			this.gpioPort.writePin(this.S3, 1);
			break;
		case RGB_X:
			this.gpioPort.writePin(this.S2, 1);
			this.gpioPort.writePin(this.S3, 0);
			break;
		default:
			break;

		}
	}

	/**
	 * Set frequency 
	 * @param f
	 * @throws IOException
	 */
	public void setFrequency(Frequency f) throws IOException {
		switch (f) {
		case HIGH:
			this.gpioPort.writePin(this.S0, 1);
			this.gpioPort.writePin(this.S1, 1);
			break;
		case MIDDLE:
			this.gpioPort.writePin(this.S0, 1);
			this.gpioPort.writePin(this.S1, 0);
			break;
		case LOW:
			this.gpioPort.writePin(this.S0, 0);
			this.gpioPort.writePin(this.S1, 1);
			break;
		case OFF:
			this.gpioPort.writePin(this.S0, 0);
			this.gpioPort.writePin(this.S1, 0);
			break;
		default:
			break;
		}
	}

	/**
	 * White balance 
	 * @throws IOException
	 */
	public void whiteBalance() throws IOException {
		setFilter(Filter.RGB_X);

		setFilter(Filter.RGB_R);
		this.counter = 0;
		Delay.msDelay(interval);
		this.Rgena = this.counter;

		setFilter(Filter.RGB_G);
		this.counter = 0;
		Delay.msDelay(interval);
		this.Ggena = this.counter;

		setFilter(Filter.RGB_B);
		this.counter = 0;
		Delay.msDelay(interval);
		this.Bgena = this.counter;
		
		System.out.println("R " + this.Rgena + " G " + this.Ggena + " B " + this.Bgena);
	}

	public int getRed() throws IOException {
		setFilter(Filter.RGB_R);
		this.counter = 0;
		Delay.msDelay(interval);

		Ramount = this.counter * 255 / Rgena; 
		if (Ramount > 255)
			Ramount = 255;

		return Ramount;
	}

	public int getGreen() throws IOException {
		setFilter(Filter.RGB_G);
		this.counter = 0;
		Delay.msDelay(interval);

		Gamount = this.counter * 255 / Ggena; // ȡGֵ
		if (Gamount > 255)
			Gamount = 255;

		return Gamount;
	}

	public int getBlue() throws IOException {
		setFilter(Filter.RGB_B);
		this.counter = 0;
		Delay.msDelay(interval);

		Bamount = this.counter * 255 / Bgena; 
		if (Bamount > 255)
			Bamount = 255;

		return Bamount;
	}

}
