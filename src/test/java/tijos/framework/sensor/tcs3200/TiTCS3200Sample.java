package tijos.framework.sensor.tcs3200;

import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.sensor.tcs3200.TiTCS3200.Frequency;
import tijos.framework.util.Delay;

/**
 * Hello world!
 *
 */
public class TiTCS3200Sample
{
	
    public static void main( String[] args )
    {
    	
		try {
			
			int gpioPort = 0;
			
			TiGPIO gpio0 = TiGPIO.open(gpioPort, 0, 1,2,3,4,5); //GPIO0 with Pin 0~5
			
			TiTCS3200 tcs3200 = new TiTCS3200(gpio0, 0, 1,2,3,4);
			tcs3200.initialize();
			tcs3200.setFrequency(Frequency.MIDDLE);
			tcs3200.whiteBalance();
			
			while(true ) {
				
				int red = tcs3200.getRed();
				int green = tcs3200.getGreen();
				int blue = tcs3200.getBlue();
				
				System.out.println(red + "," +green +"," +blue);
				
				Delay.msDelay(1000);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
