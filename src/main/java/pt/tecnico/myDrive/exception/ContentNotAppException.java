package pt.tecnico.myDrive.exception;

public class ContentNotAppException extends MyDriveException {

	/**
	 * thrown when content written to App file is invalid
	 */
	private static final long serialVersionUID = 5775708831186161742L;
	
	public ContentNotAppException() {
		super();
	}
	
	public ContentNotAppException(String reason) {
		super(reason);
	}
}
