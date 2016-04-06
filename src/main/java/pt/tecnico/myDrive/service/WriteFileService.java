package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.exception.NotPlainFileException;

public class WriteFileService extends MyDriveService {

	private long token;
	private String name;
	private String content;

	/**
	 * Default Constructor
	 */
	public WriteFileService(long token, String name, String content) {
		this.token = token;
		this.name = name;
		this.content = content;
	}

	/**
	 * Implementation of the service
	 */
	@Override
	public final void dispatch() throws NotPlainFileException {
		// FIXME:TODO:XXX
		// File plainFile = new PlainFile; //TODO get file on current directory
		// with name.
		// if(!(plainFile instanceof PlainFile))
		// throw new FileWithoutContentException();
		// ((PlainFile) plainFile).setContent(content);
	}
}
