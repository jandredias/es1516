package pt.tecnico.myDrive.exception;

/**
 * Thrown when an user tries to access a file that he has no permission to
 * access
 * 
 * @author Pedro Bucho
 *
 */
public class PermissionDeniedException extends MyDriveException {

	private static final long serialVersionUID = -7770878228433850390L;

	public PermissionDeniedException() {
		super();
	}

	public PermissionDeniedException(String reason) {
		super(reason);
	}

}
