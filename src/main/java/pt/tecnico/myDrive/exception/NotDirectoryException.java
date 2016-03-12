package pt.tecnico.myDrive.exception;

/**
 * Thrown when a Directory action is performed on a File.
 * 
 * @author Pedro Bucho
 *
 */
public class NotDirectoryException extends MyDriveException {

	private static final long serialVersionUID = -8493652119820969547L;

	public NotDirectoryException() {
		super();
	}

	public NotDirectoryException(String reason) {
		super(reason);
	}

}
