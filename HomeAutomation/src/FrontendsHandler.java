import java.util.concurrent.SynchronousQueue;

import org.zeromq.*;



public class FrontendsHandler {
	
	
	public FrontendsHandler(SynchronousQueue<String> inboundQueue,SynchronousQueue<String> outboundQueue)
	{
		ZMQ.Context context = ZMQ.context(1);
				
		InboundMonitor inboundMonitor = new InboundMonitor(context,inboundQueue);
		OutboundMonitor outboundMonitor = new OutboundMonitor(context,outboundQueue);
		
		Thread inboundThread = new Thread(inboundMonitor);
		Thread outboundThread = new Thread(outboundMonitor);
		
		inboundThread.setDaemon(false);
		outboundThread.setDaemon(false);
		
		inboundThread.start();
		outboundThread.start();
		
	}
	
	
	
	private class InboundMonitor implements Runnable
	{
		
		ZMQ.Socket inSocket;
		SynchronousQueue<String> inboundQueue;
		
		public InboundMonitor(ZMQ.Context context, SynchronousQueue<String> inboundQueue)
		{
			this.inboundQueue = inboundQueue;
			this.inSocket = context.socket(ZMQ.REP);
			this.inSocket.bind("tcp://*:5555");
			
		}

		@Override
		public void run() {
			while (true)
			{
				System.out.println("SEE THS ONCE");
				String inMessage = String.valueOf(inSocket.recv());
				System.out.println(inMessage);
				try {
					inboundQueue.put(inMessage);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class OutboundMonitor implements Runnable
	{

		
		ZMQ.Socket outSocket;
		SynchronousQueue<String> outboundQueue;
		
		public OutboundMonitor(ZMQ.Context context, SynchronousQueue<String> outboundQueue)
		{

			this.outboundQueue = outboundQueue;
			this.outSocket = context.socket(ZMQ.REQ);
			this.outSocket.connect("tcp://192.168.1.10:5555");
			
		}
		
		@Override
		public void run() {
			
			while (true)
			{
				try {
					String outMessage = ((String)outboundQueue.take());
					outSocket.send(outMessage);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	

}
