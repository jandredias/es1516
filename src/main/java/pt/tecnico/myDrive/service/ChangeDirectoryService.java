package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class ChangeDirectoryService extends MyDriveService {
	
	private MyDrive _drive;
	private long _token;
	private String _path;
	private String _returnpath;

	public ChangeDirectoryService(long token, String path) throws InvalidPathException {
		_drive = MyDriveService.getMyDrive();
		_token = token;
		if(path==null || path == "")
			throw new InvalidPathException();
		_path = path;
	}

	public final void dispatch() throws InvalidTokenException,
			FileNotFoundException, PermissionDeniedException{
		
		Session session = _drive.validateToken(_token);
		
		Directory currentDir = session.getCurrentDirectory();
		Directory targetDir = currentDir.getDirectory(_path, session.getUser());
		
		session.setCurrentDirectory(targetDir);
		
		_returnpath = targetDir.getPath();
	}
	
	public final String result() {
        return _returnpath;
    }
}