package pt.tecnico.myDrive.exception;

public class WrongPasswordException extends MyDriveException {

	private static final long serialVersionUID = 6701724737519406780L;

	public WrongPasswordException() {
		super();
	}

	public WrongPasswordException(String reason) {
		super(reason);
	}

}