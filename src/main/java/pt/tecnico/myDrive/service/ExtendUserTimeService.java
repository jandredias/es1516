package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.InvalidTokenException;

public class ExtendUserTimeService extends MyDriveService {

	private long _token;

	public ExtendUserTimeService(long token) {
		_token = token;
	}

	public final void dispatch() throws InvalidTokenException {

		MyDrive drive = MyDriveService.getMyDrive();

		drive.validateToken(_token);
	}

}
