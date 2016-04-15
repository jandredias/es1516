package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class DeleteFileService extends MyDriveService {

	private MyDrive _drive;
	private long _token;
	private String _fileName;

	/**
	 * Default Constructor
	 * @throws InvalidFileNameException
	 */
	public DeleteFileService(long token, String fileName)  {
		_drive = MyDriveService.getMyDrive();
		_token = token;
		_fileName = fileName;
	}

	/**
	 * Implementation of the service
	 * @throws InvalidTokenException
	 */
	public final void dispatch() throws InvalidTokenException,
			FileNotFoundException, MyDriveException{
		
		if(_fileName==null)
			throw new InvalidFileNameException();
		
		Session session = _drive.validateToken(_token);

		Directory currentDir = session.getCurrentDirectory();
		File targetFile = currentDir.getInnerFile(_fileName);

		targetFile.delete(session.getUser());
	}
}
