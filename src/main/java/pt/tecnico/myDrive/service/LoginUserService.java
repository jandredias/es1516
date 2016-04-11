package pt.tecnico.myDrive.service;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.InvalidPasswordException;
import pt.tecnico.myDrive.exception.WrongPasswordException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import org.apache.commons.lang.StringUtils;

public class LoginUserService extends MyDriveService {

	private String _username;
	private String _password;
	private long _token;

	public LoginUserService(String username, String password){
		this._username = username;
		this._password = password;
	}

	@Override
	public final void dispatch() throws MyDriveException {
		MyDrive myDrive = MyDrive.getInstance();

		myDrive.cleanSessions();

		if(this._username == null) throw new InvalidUsernameException();

		if(this._password == null) throw new InvalidPasswordException();

		if(this._username.equals("")) throw new InvalidUsernameException();

		if(!StringUtils.isAlphanumeric(this._username)) throw new InvalidUsernameException();

		if(this._password.equals("")) throw new WrongPasswordException();

		if(this._username.length() < 3) throw new InvalidUsernameException();

		User user = myDrive.getUserByUsername(this._username);
		if(user == null) throw new UserDoesNotExistsException();

		if(!user.getPassword().equals(this._password)) throw new WrongPasswordException();

		Session s = new Session(user, myDrive.getNewToken());

		this._token = s.getToken();
		s.setCurrentDirectory(user.getUsersHome());
	}

	public final long result() {
        return _token;
  }
}
