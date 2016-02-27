import java.util.ArrayList;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;


public class PlugDriver {

	private ArrayList<Plug> plugs;
	private final GpioController gpio = GpioFactory.getInstance();


	//The plugs are set up in this constructor
	public PlugDriver()
	{			
				
		GpioPinDigitalOutput d0 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
		GpioPinDigitalOutput d1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);
		GpioPinDigitalOutput d2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
		GpioPinDigitalOutput d3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
		
		GpioPinDigitalOutput CE = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06);
		
		//Set MODSEL
		gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05).low();
		
		// Set other pins
		CE.low();
		d0.low();
		d1.low();
		d2.low();
		d3.low();
						   //d0    d1    d2    d3
		boolean[] allOn = {true,true,false,true};
		boolean[] allOff= {true,true,false,false};
		Plug stairFairyLights = new Plug("stairFairyLights",1,allOn,allOff,d0,d1,d2,d3,CE);
		Plug tvFairyLights =    new Plug("tvFairyLights",   2,allOn,allOff,d0,d1,d2,d3,CE);

		plugs = new ArrayList<Plug>();
		plugs.add(tvFairyLights);
		plugs.add(stairFairyLights);
		

	}


	public void allOn()
	{
		System.out.println("PlugDriver\t: ALLON");
		for (Plug p : plugs)
		{
			try {
				p.turnOn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void allOff()
	{
		for (Plug p : plugs)
		{
			try {
				p.turnOff();
				Thread.sleep(300);
				p.turnOff();
				Thread.sleep(300);
				p.turnOff(); //Just incase dodgy plug doesnt turn off.
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void toggleAll()
	{
		for (Plug p :plugs)
		{
			try {
				
				if (p.getIsOn())
					p.turnOff();
				else
					p.turnOn();
			} 
			
			
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}
