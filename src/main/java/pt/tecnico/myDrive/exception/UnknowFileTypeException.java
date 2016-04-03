package pt.tecnico.myDrive.exception;

/**
 * Thrown when an user tries to create an unknow File Type!)
 * 
 * @author Pedro Bucho
 *
 */
public class UnknowFileTypeException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public UnknowFileTypeException() {
		super();
	}

	public UnknowFileTypeException(String reason) {
		super(reason);
	}

}
