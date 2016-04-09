package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;

public class ChangeDirectoryService extends MyDriveService {
	
	private MyDrive _drive;
	private long _token;
	private String _path;
	private String _returnpath;

	public ChangeDirectoryService(long token, String path) {
		_drive = MyDriveService.getMyDrive();
		_token = token;
		_path = path;
	}

	public final void dispatch() throws InvalidTokenException,
			FileNotFoundException{
		
		Session session = _drive.validateToken(_token);
		Directory dir = session.getCurrentDirectory().getDirectory(_path);
		_returnpath = dir.getPath();
	}
	
	public final String result() {
        return _returnpath;
    }
}