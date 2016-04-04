package pt.tecnico.myDrive.exception;

public class InvalidPasswordException extends MyDriveException {

	private static final long serialVersionUID = 6701724737519406780L;

	public InvalidPasswordException() {
		super();
	}

	public InvalidPasswordException(String reason) {
		super(reason);
	}

}