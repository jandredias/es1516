package pt.tecnico.myDrive.exception;


public class InvalidPathException extends MyDriveException {

	private static final long serialVersionUID = 959582543634984868L;

	public InvalidPathException() {
		super();
	}

	public InvalidPathException(String reason) {
		super(reason);
	}
}
