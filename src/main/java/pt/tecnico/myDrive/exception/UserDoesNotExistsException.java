package pt.tecnico.myDrive.exception;

public class UserDoesNotExistsException extends MyDriveException {

	private static final long serialVersionUID = 6701724737519406780L;

	public UserDoesNotExistsException() {
		super();
	}

	public UserDoesNotExistsException(String reason) {
		super(reason);
	}

}
