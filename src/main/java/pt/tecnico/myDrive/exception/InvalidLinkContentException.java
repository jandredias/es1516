package pt.tecnico.myDrive.exception;

public class InvalidLinkContentException extends MyDriveException {

	/**
	 * Thrown when content written to a Link file is invalid
	 */
	private static final long serialVersionUID = -7079697101411229552L;
	public InvalidLinkContentException() {
		super();
	}
	
	public InvalidLinkContentException(String reason) {
		super(reason);
	}
}
