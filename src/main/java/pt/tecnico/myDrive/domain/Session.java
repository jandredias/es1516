package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.Duration;

import pt.tecnico.myDrive.exception.PrivateResourceException;

public class Session extends Session_Base {

	public Session() {
		//Whenever there is a need to setUp user do this:
		// super.setUser(user);
		super();
	}

	public Session(User user, long token){
		super.setToken(token);
		super.setUser(user);
		Directory currentDirectory = user.getUsersHome();
		super.setCurrentDirectory(currentDirectory);
		DateTime lastUsed = new DateTime();
		super.setLastUsed(lastUsed);
		super.setMyDrive(user.getMyDrive());
	}

	/**FIXME REPEATED METHOD */
	public boolean valid(){
		Period p = new Period(getLastUsed(),new DateTime());
		if(p.getHours() >= 2) return false;
		return true;
	}

	/**
	 * Method that returns true when a Session is still valid
	 * @return
	 */
	protected boolean validateSession() {
		DateTime currentTime = new DateTime();
		DateTime limitTime = getLastUsed();

		//represents the amount of time between currentTime and limitTime
		Duration interval = new Duration(currentTime, limitTime);

		long miliSeconds = interval.getMillis();

		boolean valid = miliSeconds > 0;
		if (valid)
			extendTime();

		return valid;
	}

	private void extendTime() {
		DateTime currTime = new DateTime();
		currTime = currTime.plusHours(2);
		super.setLastUsed(currTime);
	}

	@Override
	public void setLastUsed(DateTime newTime) throws PrivateResourceException{
		DateTime limitTime = super.getLastUsed();

		Duration interval = new Duration(newTime, limitTime);
		if(interval.getMillis() < 0) //When Extending Limit Time
			throw new PrivateResourceException("Canot extend session time!");
		else
			super.setLastUsed(newTime);
	}

	@Override
	public void setUser(User user) throws PrivateResourceException{
		//Whenever there is a need to setUp user do this:
		//super.setUser(user);
		throw new PrivateResourceException("A User token cannot be transfered");
	}

}
