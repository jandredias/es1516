package pt.tecnico.myDrive.exception;

public class InvalidValueException extends MyDriveException {

	public InvalidValueException() {
		super();
	}
	
	public InvalidValueException(String reason) {
		super(reason);
	}
	
}
