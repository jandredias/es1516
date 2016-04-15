package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.NotPlainFileException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.InvalidTokenException;

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
	 * @throws PermissionDeniedException 
	 * @throws NotPlainFileException 
	 * @throws InvalidTokenException 
	 * @throws FileNotFoundException 
	 */ 
	@Override
	public final void dispatch() throws PermissionDeniedException, NotPlainFileException, InvalidTokenException, FileNotFoundException {
		
		MyDrive md = getMyDrive();
		Session session = md.validateToken(token);
		Directory currentDir = session.getCurrentDirectory();
		File plainFile = currentDir.getInnerFile(name);
		if(!(plainFile instanceof PlainFile))
			 throw new NotPlainFileException();
		if (!session.getUser().hasWritePermissions(plainFile)) throw new PermissionDeniedException(); 
		((PlainFile) plainFile).setContent(content, session.getUser());
		
	}
}
 