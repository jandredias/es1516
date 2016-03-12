package pt.tecnico.myDrive.exception;

public class UnsupportedOperationException extends MyDriveException {

	private static final long serialVersionUID = 6701724737519406780L;
	
	public UnsupportedOperationException() {
		super();
	}
	
	public UnsupportedOperationException(String reason) {
		super(reason);
	}
	
}
