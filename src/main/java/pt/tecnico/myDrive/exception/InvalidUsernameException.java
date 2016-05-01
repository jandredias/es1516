package pt.tecnico.myDrive.exception;

public class InvalidUsernameException extends RuntimeException {

	private static final long serialVersionUID = 5805927645300200545L;

	public InvalidUsernameException() {
		super();
	}

	public InvalidUsernameException(String reason) {
		super(reason);
	}

}
