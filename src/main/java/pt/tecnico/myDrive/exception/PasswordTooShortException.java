package pt.tecnico.myDrive.exception;


/**
 * Class for runtime errors used in MyDrive
 * @author miguel
 *
 */
public class PasswordTooShortException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PasswordTooShortException(String reason) {
		super(reason);
	}
	
	public PasswordTooShortException() {
		super();
	}
	
}
