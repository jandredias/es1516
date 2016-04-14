package pt.tecnico.myDrive.exception;


/**
 * Class for runtime errors used in MyDrive
 * @author miguel
 *
 */
public class InvalidContentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidContentException(String reason) {
		super(reason);
	}
	
}
