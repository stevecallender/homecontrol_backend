
public class Launcher{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		NetworkMonitor netMon = new NetworkMonitor();
		
		TimeMonitor timeMon = new TimeMonitor();
		
		AudioMonitor audioMon = new AudioMonitor();
		
		MediaPlayer player = new MediaPlayer();
		
		HomeController main = new HomeController(netMon,timeMon,audioMon, player);
		
		
		
		//Start threads
		
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
		
		//audioThread.start();

	}

}
