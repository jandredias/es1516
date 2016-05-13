package pt.tecnico.myDrive.exception;

public class NoExtensionFoundException extends MyDriveException {

	/**
	 * throw when executing a file and there is no extension associated with it
	 */
	private static final long serialVersionUID = -4933817827621640484L;
	
	public NoExtensionFoundException() {
		super();
	}
	
	public NoExtensionFoundException(String reason) {
		super(reason);
	}

}
