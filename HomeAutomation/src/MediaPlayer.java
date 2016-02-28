import java.io.IOException;
import java.util.concurrent.SynchronousQueue;




public class MediaPlayer implements Runnable{

	private SynchronousQueue<String> outboundQueue;
	private boolean isPlaying;
	private String currentPlayInfo = "1";

	public MediaPlayer (SynchronousQueue<String> outboundQueue)
	{
		this.outboundQueue = outboundQueue;
		isPlaying = false;
		//mopidyProcess  = Runtime.getRuntime().exec("mopidy");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BashHelper.execCommand("mpc -h localhost -p 6600");

	}	

	public void play()
	{
		if (!isPlaying)
		{
			BashHelper.execCommand("mpc play");
			isPlaying = true;
		}
	}

	public void playPlaylist(String playlist)
	{
		System.out.println("MediaPlayer\t: Starting playlist: " +playlist);
		if (!isPlaying)
		{
			BashHelper.execCommand("mpc clear");
			BashHelper.execCommand("mpc random");
			String[] playCommand = {"mpc","load",playlist};
			BashHelper.execCommand(playCommand);
			BashHelper.execCommand("mpc play");
			currentPlayInfo = BashHelper.execCommand("mpc current");

			isPlaying = true;
		}

	}
	

	public void next()
	{
		if (isPlaying)
		{
			BashHelper.execCommand("mpc next");
		}
		else
		{
			BashHelper.execCommand("mpc play");
			BashHelper.execCommand("mpc next");
			isPlaying = true;
		}
	}


	public void previous()
	{
		if (isPlaying)
		{
			BashHelper.execCommand("mpc prev");
		}
		else
		{
			BashHelper.execCommand("mpc play");
			BashHelper.execCommand("mpc prev");
			isPlaying = true;
		}
	}

	public void pause()
	{
		if (isPlaying)
		{
			BashHelper.execCommand("mpc pause");
			isPlaying = false;
		}
	}

	private void handlePlayInfo(String newInfo)
	{
		//check here for an actual change
		if (newInfo.compareTo(currentPlayInfo) != 0)
		{
			try {
				outboundQueue.put("1"+newInfo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			currentPlayInfo = newInfo;

		}


	}

	@Override
	public void run() {

		while (true)
		{
			handlePlayInfo(BashHelper.execCommand("mpc current"));
		}

	}




}
