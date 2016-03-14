package pt.tecnico.myDrive.exception;

public class FileExistsException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public FileExistsException() {
		super();
	}
	
	public FileExistsException(String reason) {
		super(reason);
	}
	
}
