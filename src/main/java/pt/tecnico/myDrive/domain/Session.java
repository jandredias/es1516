package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import pt.tecnico.myDrive.exception.InvalidTokenException;

public class Session extends Session_Base {

	public Session() {
		super();
	}

	public boolean isStillValid() {
		DateTime currentTime = new DateTime();
		DateTime limitTime = getLastUsed();
		
		//represents the amount of time between currentTime and limitTime
		Duration interval = new Duration(currentTime, limitTime); 
		
		long miliSeconds = interval.getMillis();
		
		return miliSeconds > 0;
	}

	public void extendTime() {
		DateTime currTime = new DateTime();
		currTime = currTime.plusHours(2);
		setLastUsed(currTime);
	}
}
