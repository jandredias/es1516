package pt.tecnico.myDrive.exception;

/**
 * Generic MyDrive exception
 * @author Pedro Bucho
 *
 */
public class MyDriveException extends Exception {

	private static final long serialVersionUID = -5495644594099515732L;
	
	public MyDriveException() {
		super();
	}
	
	public MyDriveException(String reason) {
		super(reason);
	}
	
}
