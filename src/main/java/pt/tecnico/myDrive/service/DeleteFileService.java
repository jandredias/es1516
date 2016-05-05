package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class DeleteFileService extends MyDriveService {

	private long _token;
	private String _fileName;

	/**
	 * Default Constructor
	 * @throws InvalidFileNameException
	 */
	public DeleteFileService(long token, String fileName)  {
		_token = token;
		_fileName = fileName;
	}

	/**
	 * Implementation of the service
	 * @throws InvalidTokenException
	 * @throws InvalidFileNameException 
	 * @throws PermissionDeniedException 
	 */
	@Override
	public final void dispatch() throws InvalidTokenException,
			FileNotFoundException, InvalidFileNameException, PermissionDeniedException{
		
		if(_fileName==null)
			throw new InvalidFileNameException();
		
		MyDrive drive = MyDriveService.getMyDrive();
		
		Session session = drive.validateToken(_token);

		Directory currentDir = session.getCurrentDirectory();
		File targetFile = currentDir.getInnerFile(_fileName);

		targetFile.delete(session.getUser());
	}
}
