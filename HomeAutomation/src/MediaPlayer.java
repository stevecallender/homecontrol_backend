import java.io.IOException;




public class MediaPlayer{


	private Process mopidyProcess;
	private Process clientProcess;
	private boolean isPlaying;

	public MediaPlayer ()
	{

		isPlaying = false;
		try {
			//mopidyProcess  = Runtime.getRuntime().exec("mopidy");
			Thread.sleep(5000);
			clientProcess   = Runtime.getRuntime().exec("mpc -h localhost -p 6600");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}								
	}	
	
	
	public void play(String playlist)
	{
		if (!isPlaying)
		{
			try {
				clientProcess   = Runtime.getRuntime().exec("mpc clear");
				clientProcess   = Runtime.getRuntime().exec("mpc random");
				String[] playCommand = {"mpc","load",playlist};
				clientProcess   = Runtime.getRuntime().exec(playCommand);
				clientProcess.waitFor();
				clientProcess   = Runtime.getRuntime().exec("mpc play");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isPlaying = true;
		}


	}

	public void stop()
	{
		if (isPlaying)
		{
			try {
				clientProcess   = Runtime.getRuntime().exec("mpc pause");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			isPlaying = false;
		}
	}


	
	
}
