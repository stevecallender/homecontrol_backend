import com.pi4j.io.gpio.GpioPinDigitalOutput;


public class Plug {

	private String name;
	private boolean isOn = false; 
	private boolean[] onCombination = {true,true,false,true}; 
	private boolean[] offCombination = {true,true,false,false}; 

	private GpioPinDigitalOutput d0;
	private GpioPinDigitalOutput d1;
	private GpioPinDigitalOutput d2;
	private GpioPinDigitalOutput d3;

	private GpioPinDigitalOutput CE;


	public Plug (	String name,
			int num, 
			boolean[] onCombination,
			boolean[] offCombination,
			GpioPinDigitalOutput d0,
			GpioPinDigitalOutput d1,
			GpioPinDigitalOutput d2,
			GpioPinDigitalOutput d3,
			GpioPinDigitalOutput CE){

		this.name = name;

		this.onCombination[0] = onCombination[0];
		this.onCombination[1] = onCombination[1];
		this.onCombination[2] = onCombination[2];
		this.onCombination[3] = onCombination[3];

		this.offCombination[0] = offCombination[0];
		this.offCombination[1] = offCombination[1];
		this.offCombination[2] = offCombination[2];
		this.offCombination[3] = offCombination[3];

		this.d0 = d0;
		this.d1 = d1;
		this.d2 = d2;
		this.d3 = d3;
		this.CE = CE;
	}

	public boolean getIsOn()
	{
		return isOn;
	}

	public void turnOn() throws InterruptedException
	{
		System.out.println("Plug\t\t: "+ name + " turning on");

		for (int i = 0; i < 3; i++)
		{

			if (onCombination[0]) d0.high(); else d0.low();
			if (onCombination[1]) d1.high(); else d1.low();
			if (onCombination[2]) d2.high(); else d2.low();
			if (onCombination[3]) d3.high(); else d3.low();


			Thread.sleep(100);

			CE.high();

			Thread.sleep(750);

			CE.low();

		}

		isOn = true;
	}

	public void turnOff() throws InterruptedException
	{
		System.out.println("Plug\t\t: "+ name + " turning off");

		for (int i = 0; i < 3; i++)
		{

			if (offCombination[0]) d0.high(); else d0.low();
			if (offCombination[1]) d1.high(); else d1.low();
			if (offCombination[2]) d2.high(); else d2.low();
			if (offCombination[3]) d3.high(); else d3.low();

			Thread.sleep(100);

			CE.high();

			Thread.sleep(750);

			CE.low();
		}

		isOn = false;
	}

	public String getName()
	{
		return name;
	}



}
