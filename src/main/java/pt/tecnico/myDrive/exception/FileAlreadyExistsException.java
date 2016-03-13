package pt.tecnico.myDrive.exception;

public class FileAlreadyExistsException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public FileAlreadyExistsException() {
		super();
	}
	
	public FileAlreadyExistsException(String reason) {
		super(reason);
	}
	
}
