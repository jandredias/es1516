package pt.tecnico.myDrive.exception;

public class InvalidPasswordException extends MyDriveException {

	private static final long serialVersionUID = 5805927645300200545L;

	public InvalidPasswordException() {
		super();
	}

	public InvalidPasswordException(String reason) {
		super(reason);
	}

}
