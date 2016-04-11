package pt.tecnico.myDrive.exception;

public class FileIsDirectoryException extends MyDriveException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2277713649793633648L;
	
	public FileIsDirectoryException() {
		super();
	}
	
	public FileIsDirectoryException(String reason) {
		super(reason);
	}
}
