import java.io.UnsupportedEncodingException;
import java.util.Arrays;
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
				String inMessage = "";
				try {
					inMessage = new String(inSocket.recv(),"UTF8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("request received: " + inMessage);
				try {
					inboundQueue.put(inMessage);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				inSocket.send("1");
				System.out.println("response sent");
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
			this.outSocket.connect("tcp://192.168.1.10:5556");
			
		}
		
		@Override
		public void run() {
			
			while (true)
			{
				try {
					String outMessage = ((String)outboundQueue.take());
					System.out.println("sending request: " + outMessage);
					outSocket.send(outMessage);
					String resp = "";
					try {
						resp = new String(outSocket.recv(),"UTF8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("response received: " +resp);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
				
			}
			
		}
		
	}
	

}
