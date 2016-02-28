import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.SynchronousQueue;


public class TimeMonitor implements Runnable{


	private ArrayList<TimeObserver> observers;
	SynchronousQueue<String> outboundQueue;

	public TimeMonitor(SynchronousQueue<String> outboundQueue)
	{
		observers = new ArrayList<TimeObserver>();
		this.outboundQueue = outboundQueue;
	}


	public void subscribeMeToUpdates(TimeObserver observer)
	{
		observers.add(observer);
	}


	@Override
	public void run() {

		System.out.println("TimeMonitor\t: Time Monitor Starting");

		DateFormat hourFormatter = new SimpleDateFormat("HH");

		DateFormat minFormatter = new SimpleDateFormat("mm");

		DateFormat dayFormatter = new SimpleDateFormat("EEE");

		while (true)
		{

			Date date = new Date(System.currentTimeMillis());

			String day = dayFormatter.format(date);

			int hour = Integer.valueOf((hourFormatter.format(date))).intValue();

			int min = Integer.valueOf((minFormatter.format(date))).intValue();
			
			try {
				outboundQueue.put("4"+ day + " " + hour +":" + min);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			//System.out.println("TimeMonitor\t: Current Day:" + day + ", Hour: " + hour +", Minute: " +min);

			if (day.equalsIgnoreCase("sat") || day.equalsIgnoreCase("sun"))
			{

				if (hour >= 10 && hour <= 23)					
				{
					System.out.println("TimeMonitor\t: Morning - System active");
					for (TimeObserver o : observers) o.goodMorning();
				}
				else
				{
					System.out.println("TimeMonitor\t: Dead time - System off");
					for (TimeObserver o : observers) o.standDown();
				}
			} 

			else
			{

				if (hour >= 6 && hour <= 11)			
				{
					if (min >= 30)
					{
						System.out.println("TimeMonitor\t: Morning - System active");
						for (TimeObserver o : observers) o.goodMorning();
					}
				}
				else if (hour >= 15 && hour <= 23)
				{
					System.out.println("TimeMonitor\t: Evening - System active");
					for (TimeObserver o : observers) o.goodEvening();
				}
				else
				{
					System.out.println("TimeMonitor\t: Dead time - System off");
					for (TimeObserver o : observers) o.standDown();
				}

			}


			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}




}
