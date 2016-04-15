package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class ChangeDirectoryService extends MyDriveService {

	private MyDrive _drive;
	private long _token;
	private String _path;
	private String _returnpath;

	public ChangeDirectoryService(long token, String path){
		_drive = MyDriveService.getMyDrive();
		_token = token;
		_path = path;
	}

	public final void dispatch() throws FileNotFoundException, InvalidTokenException, PermissionDeniedException {

		Session session = _drive.validateToken(_token);

		if(_path==null || _path.equals(""))
			throw new FileNotFoundException();


		Directory currentDir = session.getCurrentDirectory();

		Directory targetDir;
		if(_path.charAt(0) == '/'){
			//System.out.println("\u001B[33mGoing for root\u001B[0m");
			targetDir = _drive.getRootDirectory().getDirectory(_path,session.getUser());
		}
		else{
			//System.out.println("\u001B[33mGoing for Relative\u001B[0m");
			targetDir = currentDir.getDirectory(_path, session.getUser());
		}
		if(!session.getUser().hasExecutePermissions(targetDir)) throw new PermissionDeniedException();
		session.setCurrentDirectory(targetDir);

		_returnpath = targetDir.getPath();
	}

	public final String result() {
		return _returnpath;
	}
}
