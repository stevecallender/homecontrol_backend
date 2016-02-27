
public class Device {
	
	public String name;
	public String macAddress;
	public boolean isConnected;
	
	public Device (String name, String macAddress)
	{
		this.name = name;
		this.macAddress = macAddress;
		this.isConnected = false;
	}
	
	@Override
	public boolean equals(Object d)
	{
		return ((Device)d).macAddress.equalsIgnoreCase(macAddress);
	}

}
