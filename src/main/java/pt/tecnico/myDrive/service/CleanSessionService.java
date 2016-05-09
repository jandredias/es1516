package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.MyDriveException;

public class CleanSessionService extends MyDriveService {
	
	private long _token;
	
	/**
	 * Default Constructor
	 * @throws InvalidFileNameException
	 */
	public CleanSessionService(long token)  {
		_token = token;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		MyDriveService.getMyDrive().eraseSession(_token);
	}

}
