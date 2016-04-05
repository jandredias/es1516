package pt.tecnico.myDrive.service;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.MyDriveException;

public class LoginUserService extends MyDriveService {

	private String _username;
	private String _password;

	public LoginUserService(String username, String password){
		this._username = username;
		this._password = password;
	}

	@Override
	public final void dispatch() throws MyDriveException {
		
		//TODO
		throw new MyDriveException("Service not implemented yet");
	}
}


