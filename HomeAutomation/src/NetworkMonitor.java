import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class NetworkMonitor implements Runnable{

	private ArrayList<NetworkObserver> observers;
	private ArrayList<Device> devicesOnNetwork;



	public NetworkMonitor()
	{
		observers = new ArrayList<NetworkObserver>();
		devicesOnNetwork = new ArrayList<Device>();
	}


	public void subscribeMeToUpdates(NetworkObserver observer)
	{
		observers.add(observer);

		//Must check that a new interested device has not just joined
		for (Device i : observer.getInterestedDevices())
		{
			if (devicesOnNetwork.contains(i))
			{
				for (NetworkObserver n: observers)
				{
					n.deviceJoinedNetwork(i);

				}
			}

		}

	}

	private void newDeviceJoined(Device newDevice)
	{
		if (!devicesOnNetwork.contains(newDevice)){

			devicesOnNetwork.add(newDevice);

			for (NetworkObserver o:observers)
			{
				if (o.getInterestedDevices().contains(newDevice))
				{
					o.deviceJoinedNetwork(newDevice);
				}
			}
		}
	}

	private void deviceLeft(Device oldDevice)
	{
		if (devicesOnNetwork.contains(oldDevice))
		{
			devicesOnNetwork.remove(oldDevice);

			for (NetworkObserver o:observers)
			{
				if (o.getInterestedDevices().contains(oldDevice))
				{
					o.deviceLeftNetwork(oldDevice);
				}

			}
		}
	}


	private String execCommand(String command) throws IOException, InterruptedException
	{
		String returnString = "";

		Process p = Runtime.getRuntime().exec(command);

		p.waitFor();		

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));


		String line = reader.readLine();

		while (line != null)
		{
			returnString += line + '\n';
			line = reader.readLine();
		}

		reader.close();

		return returnString;

	}

	private String getIpAddressForMac(String arpResult, String macAddress) throws IOException
	{
		String returnString = "_NO_IP_";
		if (arpResult != ""){

			String[] resultArray = arpResult.split("\n");

			for (int i = 0; i < resultArray.length; i++)
			{
				if (resultArray[i].contains(macAddress))
					returnString = resultArray[i].split(" ")[1];
			}

		}

		returnString = returnString.substring(1,returnString.length()-1);

		return returnString;
	}





	@Override
	public void run() {

		int count = 0;

		boolean emmaPresent = false;
		boolean stevePresent = false;

		int emmaThreshold  = 300;
		int steveThreshold = 300;

		String emmasIP = "";
		String steveIP = "";


		System.out.println("NetworkMonitor\t: Network Monitor Starting");

		try {

			//Ping the network (IPs should be in range 1-10)
			execCommand("fping -m -g 192.168.1.1 192.168.1.10");

			emmasIP = getIpAddressForMac(execCommand("arp -an"),"b4:18:d1:d3:35:4e");
			steveIP = getIpAddressForMac(execCommand("arp -an"),"1c:1a:c0:22:23:e7");

			Device stevePhone = new Device("StevesPhone", "1c:1a:c0:22:23:e7");
			Device emmaPhone = new Device("EmmasPhone", "b4:18:d1:d3:35:4e");

			System.out.println("NetoworkMonitor\t: EmmasIP:" + emmasIP);
			System.out.println("NetoworkMonitor\t: SteveIP:" + steveIP);


			while (true){				

				emmaPresent = false;
				stevePresent = false;

				String result = execCommand("fping -m -g 192.168.1.1 192.168.1.10");

				String[] resultSet = result.split("\n");

				for (int i = 0; i < resultSet.length; i++)
				{
					String resultRow = resultSet[i];

					if (resultRow.contains("alive") && resultRow.contains(emmasIP) ){
						newDeviceJoined(emmaPhone);
						emmaPresent = true;
						if (stevePresent)
							break;
					}

					if (resultRow.contains("alive") && resultRow.contains(steveIP) ){
						newDeviceJoined(stevePhone);
						stevePresent = true;
						if (emmaPresent)
							break;
					}	

				}

//				if (stevePresent)
//					steveThreshold = 150;
//				else{
//					steveThreshold --;
//					//steveIP = getIpAddressForMac(execCommand("arp -a"),"1c:1a:c0:22:23:e7");
//				}
//
//				if (emmaPresent)
//					emmaThreshold = 150;
//				else{
//					emmaThreshold --;
//					//emmasIP = getIpAddressForMac(execCommand("arp -a"),"b4:18:d1:d3:35:4e");
//				}
//				
//				
//				if (emmaThreshold < 0)
//				{
//					deviceLeft(emmaPhone);
//					emmaThreshold = 150;
//				}
//				if (steveThreshold < 0)
//				{
//					deviceLeft(stevePhone);
//					steveThreshold = 150;
//				}
				
				
				
				if (stevePresent)
					steveThreshold = 300;
				else if (steveThreshold-- < 0){
					deviceLeft(stevePhone);
					steveThreshold = 300;
				}
				else{
					steveIP = getIpAddressForMac(execCommand("arp -an"),"1c:1a:c0:22:23:e7");
				}

				if (emmaPresent)
					emmaThreshold = 300;
				else if(emmaThreshold-- < 0){
					deviceLeft(emmaPhone);
					emmaThreshold = 300;
				}
				else{
					emmasIP = getIpAddressForMac(execCommand("arp -an"),"b4:18:d1:d3:35:4e");
				}
				
				
				
				
				
					
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}



	}

}
