package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.WrongPasswordException;

public class LoginUserService extends MyDriveService {

	private String _username;
	private String _password;
	private long _token;

	public LoginUserService(String username, String password){
		this._username = username;
		this._password = password;
	}

	@Override
	public final void dispatch() throws UserDoesNotExistsException, WrongPasswordException{

		this._token = MyDriveService.getMyDrive().login(_username,_password);
	}

	public final long result() {
        return _token;
  }
}
