package pt.tecnico.myDrive.exception;

public class InvalidAppContentException extends MyDriveException {

	/**
	 * thrown when content written to App file is invalid
	 */
	private static final long serialVersionUID = 5775708831186161742L;
	
	public InvalidAppContentException() {
		super();
	}
	
	public InvalidAppContentException(String reason) {
		super(reason);
	}
}
