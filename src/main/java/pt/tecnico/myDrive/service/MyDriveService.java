package pt.tecnico.myDrive.service;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.phonebook.exception.PersonDoesNotExistException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MyDriveService{
	protected static final Logger log = LogManager.getRootLogger();

	@Atomic
	public final void execute() throws MyDriveException {
		dispatch();
	}

	static MyDrive getMyDrive() {
		return MyDrive.getInstance();
	}
	
	protected User getUser( String username ){
		User user = getMyDrive().getUserByUsername(username);
		if (user == null)
			throw new UserDoesNotExistsException(username);

		return user;	
	}
	

	
	protected abstract void dispatch() throws MyDriveException;
}
