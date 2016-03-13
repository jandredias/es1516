package pt.tecnico.myDrive.exception;

public class FIleAlreadyExistsException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public FIleAlreadyExistsException() {
		super();
	}
	
	public FIleAlreadyExistsException(String reason) {
		super(reason);
	}
	
}
