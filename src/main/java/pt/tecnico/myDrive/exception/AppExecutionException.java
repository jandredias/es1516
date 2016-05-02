package pt.tecnico.myDrive.exception;


/**
 * Class for runtime errors used in MyDrive
 * @author miguel
 *
 */
public class AppExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AppExecutionException(String reason) {
		super(reason);
	}
	
}
