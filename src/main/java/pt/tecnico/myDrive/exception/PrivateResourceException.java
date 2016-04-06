package pt.tecnico.myDrive.exception;


/**
 * Class for runtime errors used in MyDrive
 * @author miguel
 *
 */
public class PrivateResourceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PrivateResourceException(String reason) {
		super(reason);
	}
	
}
