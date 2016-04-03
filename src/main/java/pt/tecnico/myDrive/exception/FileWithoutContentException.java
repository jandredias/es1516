package pt.tecnico.myDrive.exception;

public class FileWithoutContentException extends MyDriveException {

	private static final long serialVersionUID = 5616515165L;

	public FileWithoutContentException() {
		super();
	}

	public FileWithoutContentException(String reason) {
		super(reason);
	}

}
