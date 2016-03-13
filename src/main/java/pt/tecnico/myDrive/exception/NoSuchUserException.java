package pt.tecnico.myDrive.exception;

public class NoSuchUserException extends MyDriveException {

	private static final long serialVersionUID = 6701724737519406780L;

	public NoSuchUserException() {
		super();
	}

	public NoSuchUserException(String reason) {
		super(reason);
	}

}
