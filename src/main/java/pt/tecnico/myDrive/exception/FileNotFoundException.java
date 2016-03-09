package pt.tecnico.myDrive.exception;

/**
 * Thrown when an user tries to access a file that does not exist (not to be
 * confused with java.io.FileNotFoundException!)
 * 
 * @author Pedro Bucho
 *
 */
public class FileNotFoundException extends MyDriveException {

	private static final long serialVersionUID = 959582543634984868L;

	public FileNotFoundException() {
		super();
	}

	public FileNotFoundException(String reason) {
		super(reason);
	}

}
