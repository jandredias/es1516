package pt.tecnico.myDrive.exception;

/**
 * Thrown when an user tries to access a file that he has no permission to
 * access
 * 
 * @author Pedro Bucho
 *
 */
public class InvalidTokenException extends MyDriveException {

	private static final long serialVersionUID = -7770878228433850390L;

	public InvalidTokenException() {
		super();
	}

	public InvalidTokenException(String reason) {
		super(reason);
	}

}
