import java.util.ArrayList;


public interface NetworkObserver {
	
	void deviceJoinedNetwork(Device d);
	void deviceLeftNetwork(Device d);
	ArrayList<Device> getInterestedDevices();

}
