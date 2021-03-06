import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;



/*
 * TODO Updates:
 * 		- Update the code to stay on in the afternoon if there is an interested device connected but still go off in evening
 * 		- Sort out the threading issues and make the inter thread comms thread safe - shouldReact for example...
 * 		- Update to intercept bluetooth feedback
 * 		- Get mopidy server running from in this program
 * 		- Add in hilarious voice output
 * 		- Look into checking the mopidy status before issuing commands
 */



public class HomeController implements NetworkObserver,TimeObserver, ClapObserver, Runnable{

	private ArrayList<Device>interestedDevices;
	private PlugDriver plugDriver;
	private MediaPlayer player;
	private boolean shouldReact;

	private SynchronousQueue<String> inboundQueue = new SynchronousQueue<String>();
	private SynchronousQueue<String> outboundQueue = new SynchronousQueue<String>();


	public HomeController(SynchronousQueue<String> inboundQueue,
			SynchronousQueue<String> outboundQueue,
			NetworkMonitor netMonitor,
			TimeMonitor timeMonitor,
			AudioMonitor audioMonitor,
			MediaPlayer player)
	{
		this.inboundQueue = inboundQueue;
		this.outboundQueue = outboundQueue;
		interestedDevices = new ArrayList<Device>();
		interestedDevices.add(new Device("EmmasPhone", "b4:18:d1:d3:35:4e"));
		interestedDevices.add(new Device("StevesPhone", "1c:1a:c0:22:23:e7"));

		plugDriver = new PlugDriver();
		shouldReact = false;
		timeMonitor.subscribeMeToUpdates(this);
		netMonitor.subscribeMeToUpdates(this);
		this.player = player;
		BluetoothManager.connect();
		//audioMonitor.subscribeMeToUpdates(this);


		new FrontendsHandler(inboundQueue, outboundQueue);

	}

	@Override
	public void deviceJoinedNetwork(Device d) {
		System.out.println("HomeController\t: "+d.name+"'s device joined: "+d.macAddress);
		interestedDevices.get(interestedDevices.indexOf(d)).isConnected = true;
		if (shouldReact){
			plugDriver.allOn();
			try {
				outboundQueue.put("2lightsOn");
				outboundQueue.put("3play");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BluetoothManager.connect();
			player.playPlaylist("Classical");
			shouldReact = false;
		}

	}

	@Override
	public void deviceLeftNetwork(Device d) {
		System.out.println("HomeController\t: "+d.name+"'s device left: "+d.macAddress);
		interestedDevices.get(interestedDevices.indexOf(d)).isConnected = false;
		if (!anyInterestedConnected())
		{
			plugDriver.allOff();
			try {
				outboundQueue.put("2lightsOff");
				outboundQueue.put("3play");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			player.pause();
			shouldReact = true;
		}
	}

	@Override
	public ArrayList<Device> getInterestedDevices() {
		return interestedDevices;
	}

	private boolean allInterestedConnected()
	{
		boolean allConnected = true;

		for (Device d : interestedDevices)
		{
			if (!d.isConnected)
				allConnected = false;
		}

		return allConnected;

	}

	private boolean anyInterestedConnected()
	{
		boolean anyConnected = false;

		for (Device d : interestedDevices)
		{
			if (d.isConnected)
				anyConnected = true;
		}

		return anyConnected;

	}

	@Override
	public void goodMorning() {
		if (!shouldReact)
		{
			System.out.println("HomeController\t: GOOD MORNING!");
			shouldReact = true;
			BluetoothManager.connect();
			player.playPlaylist("Your Coffee Break (by spotify_uk_)");
			plugDriver.allOn();
			try {
				outboundQueue.put("2lightsOn");
				outboundQueue.put("3play");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void goodEvening() {
		if (!shouldReact)
		{
			System.out.println("HomeController\t: GOOD EVENING!");
			shouldReact = true;
		}

	}

	@Override
	public void standDown() {
		if (shouldReact)
		{
			System.out.println("HomeController\t: GOODBYE!");
			plugDriver.allOff();
			try {
				outboundQueue.put("2lightsOff");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			shouldReact = false;
			player.pause();
		}
	}

	@Override
	public void clapDetected() {
		plugDriver.toggleAll();
	}


	public void run()
	{
		while (true)
		{
			try {
				String msg = inboundQueue.take();

				//System.out.println(msg);

				if (msg.equalsIgnoreCase("play"))
				{
					player.play();
					outboundQueue.put("3play");
				}
				else if (msg.equalsIgnoreCase("pause"))
				{
					player.pause();
					outboundQueue.put("3pause");
				}
				else if (msg.equalsIgnoreCase("next"))
				{
					player.next();
					outboundQueue.put("3play");
				}
				else if (msg.equalsIgnoreCase("prev"))
				{
					player.previous();
					outboundQueue.put("3play");
				}
				else if (msg.equalsIgnoreCase("lightsOn"))
				{
					plugDriver.allOn();
					outboundQueue.put("2lightsOn");
				}
				else if (msg.equalsIgnoreCase("lightsOff"))
				{
					plugDriver.allOff();
					outboundQueue.put("2lightsOff");

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
