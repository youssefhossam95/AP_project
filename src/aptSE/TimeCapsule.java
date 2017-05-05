package aptSE;
import java.time.LocalTime;

public class TimeCapsule {
	
	LocalTime recordedTime;
	float delay;
	TimeCapsule(float delay)
	{
		this.delay=delay;
		recordedTime=LocalTime.now();
	}
	public synchronized void updateTime() //renews the blocking of the host.
	{
		recordedTime=LocalTime.now();
	}
	public synchronized boolean isBlocked() //Checks if host is currently blocked.
	{
		if(recordedTime.getHour()!=LocalTime.now().getHour())
			return false;
		if(recordedTime.getMinute()!=LocalTime.now().getMinute())
			return false;
		if(recordedTime.getSecond()+delay<LocalTime.now().getSecond())
			return false;
		return true;
	}
	
}
