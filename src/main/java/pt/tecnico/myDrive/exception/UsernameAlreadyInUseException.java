package pt.tecnico.myDrive.exception;

public class UsernameAlreadyInUseException extends MyDriveException {

	private static final long serialVersionUID = 6701724737519406780L;

	public UsernameAlreadyInUseException() {
		super();
	}

	public UsernameAlreadyInUseException(String reason) {
		super(reason);
	}

}
