package pt.tecnico.myDrive.exception;

public class DirectoryIsNotEmptyException extends MyDriveException {

	private static final long serialVersionUID = 2817576747512572137L;
	
	public DirectoryIsNotEmptyException() {
		super();
	}
	
	public DirectoryIsNotEmptyException(String reason) {
		super(reason);
	}
	
}
