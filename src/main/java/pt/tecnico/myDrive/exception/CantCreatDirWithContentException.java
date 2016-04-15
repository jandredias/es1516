package pt.tecnico.myDrive.exception;

public class CantCreatDirWithContentException extends MyDriveException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2277713649793633648L;
	
	public CantCreatDirWithContentException() {
		super();
	}
	
	public CantCreatDirWithContentException(String reason) {
		super(reason);
	}
}
