package pt.tecnico.myDrive.exception;


/**
 * Class for runtime errors used in MyDrive
 * @author miguel
 *
 */
public class TestSetupException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TestSetupException(String reason) {
		super("\u001B[31;1m"+"TEST ERROR : " +reason+ "\u001B[0m");
	}
	
}
