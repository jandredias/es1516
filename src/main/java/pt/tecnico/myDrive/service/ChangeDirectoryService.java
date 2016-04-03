package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.MyDriveException;

public class ChangeDirectoryService extends MyDriveService {
	
	private long _token;
	private String _path;
	private String _currentDir;
	private String _returnpath;

	public ChangeDirectoryService(long token, String path, String currentDir) {
		_token = token;
		_path = path;
		_currentDir = currentDir;
	}

	public final void dispatch() throws MyDriveException {
		MyDrive md = getMyDrive();
		
		//user permission TODO
		
		//TODO
	}
	
	public final String result() {
        return _returnpath;
    }
}