import java.util.concurrent.SynchronousQueue;


public class Launcher{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SynchronousQueue<String> inboundQueue = new SynchronousQueue<String>();
		SynchronousQueue<String> outboundQueue = new SynchronousQueue<String>();
		
		NetworkMonitor netMon = new NetworkMonitor();
		
		TimeMonitor timeMon = new TimeMonitor(outboundQueue);
		
		AudioMonitor audioMon = new AudioMonitor();
		
		MediaPlayer player = new MediaPlayer(outboundQueue);
		
		HomeController main = new HomeController(inboundQueue,outboundQueue,netMon,timeMon,audioMon, player);
		
		
		
		//Start threads
		
		Thread playerThread = new Thread(player);
		
		playerThread.setDaemon(false);
		
		Thread monitorThread = new Thread(netMon);
		
		monitorThread.setDaemon(false);
		
		Thread clockThread = new Thread(timeMon);
		
		clockThread.setDaemon(false);
		
		Thread mainThread = new Thread(main);
		
		mainThread.setDaemon(false);
		
		//Thread audioThread = new Thread(audioMon);
		
		//audioThread.setDaemon(false);
		
		mainThread.start();
		
		monitorThread.start();
		
		clockThread.start();
		
		playerThread.start();
		
		//audioThread.start();

	}

}
