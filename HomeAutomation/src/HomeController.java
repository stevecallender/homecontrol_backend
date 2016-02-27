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
	
	public HomeController(NetworkMonitor netMonitor,
						  TimeMonitor timeMonitor,
						  AudioMonitor audioMonitor,
						  MediaPlayer player)
	{
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
			BluetoothManager.connect();
			player.play("Classical");
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
			player.stop();
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
			player.play("Your Coffee Break (by spotify_uk_)");
			try {
				this.outboundQueue.put("Your Coffe Break");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			plugDriver.allOn();
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
			shouldReact = false;
			player.stop();
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
				
				System.out.println(msg);
				
				if (msg.equalsIgnoreCase("play"))
				{
					player.play("...");
					this.outboundQueue.put("Your Coffe Break");
				}
				else if (msg.equalsIgnoreCase("pause"))
					player.stop();
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
