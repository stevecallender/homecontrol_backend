import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;




public class AudioMonitor implements Runnable{
	
	private ArrayList<ClapObserver> observers;
	
	
	public AudioMonitor()
	{
		observers = new ArrayList<ClapObserver>();
	}


	public void subscribeMeToUpdates(ClapObserver observer)
	{
		observers.add(observer);
	}
	
	
	private void clapDetected()
	{
		for (ClapObserver o : observers)
		{
			o.clapDetected();
		}
	}
	
	
	
	@Override
	public void run() {
		
        ByteArrayOutputStream byteArrayOutputStream;
        TargetDataLine targetDataLine;
        int cnt;
        boolean stopCapture = false;
        byte tempBuffer[] = new byte[8000];
        int countzero;  
        short convert[] = new short[tempBuffer.length];
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            stopCapture = false;
            while (!stopCapture) {
                AudioFormat audioFormat = new AudioFormat(8000.0F, 16, 1, true, false);
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
                targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                byteArrayOutputStream.write(tempBuffer, 0, cnt);
                try {
                    countzero = 0;
                    for (int i = 0; i < tempBuffer.length; i++) {                                   
                        convert[i] = (short)tempBuffer[i];
                        if (convert[i] == 0) {
                            countzero++;
                        }
                    }
                    if (countzero <= 1300){
                    	clapDetected();
                    }

                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
                Thread.sleep(0);
                targetDataLine.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
		
		
	}

}
