package pt.tecnico.myDrive.exception;

public class ContentNotLinkException extends MyDriveException {

	/**
	 * Thrown when content written to a Link file is invalid
	 */
	private static final long serialVersionUID = -7079697101411229552L;
	public ContentNotLinkException() {
		super();
	}
	
	public ContentNotLinkException(String reason) {
		super(reason);
	}
}
