import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class BashHelper {


	public static String execCommand(String command)
	{
		String returnString = "";

		try{

			Process p = Runtime.getRuntime().exec(command);

			p.waitFor();		

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));


			String line = reader.readLine();

			while (line != null)
			{
				returnString += line + '\n';
				line = reader.readLine();
			}

			reader.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return returnString;

	}


	//'bit' of  hack at the moment... to allow the execution of commands with string arrays... yolo
	public static String execCommand(String[] command) 
	{
		String returnString = "";

		Process p;


		try {
			p = Runtime.getRuntime().exec(command);

			p.waitFor();		

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));


			String line = reader.readLine();

			while (line != null)
			{
				returnString += line + '\n';
				line = reader.readLine();
			}

			reader.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return returnString;

	}

}
