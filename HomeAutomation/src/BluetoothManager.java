

public class BluetoothManager {
	
	public static void connect()
	{
		try {
			Process bluetoothConnect = Runtime.getRuntime().exec("bluez-test-audio connect FC:58:FA:6C:42:43");
			bluetoothConnect.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
