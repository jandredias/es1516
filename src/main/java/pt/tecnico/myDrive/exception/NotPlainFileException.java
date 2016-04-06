package pt.tecnico.myDrive.exception;

/**
 * Thrown when a Directory action is performed on a File.
 * 
 * @author Pedro Bucho
 *
 */
public class NotPlainFileException extends MyDriveException {

	private static final long serialVersionUID = -8493652119820969547L;

	public NotPlainFileException() {
		super();
	}

	public NotPlainFileException(String reason) {
		super(reason);
	}

}
