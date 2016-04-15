package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.FileContentsVisitor;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class ReadFileService extends MyDriveService {

	private MyDrive _drive;
	private String  _fileName;
	private long 	_token;
	private String	_content;
	
	public ReadFileService(long token, String fileName) {
		_drive = MyDriveService.getMyDrive();
		_fileName = fileName;
		_token = token;
	}
	
	@Override
	protected void dispatch() throws InvalidTokenException, PermissionDeniedException, FileNotFoundException, NotDirectoryException, UnsupportedOperationException {
		
		Session session = _drive.validateToken(_token);
		Directory currentDir = session.getCurrentDirectory();
		File file = currentDir.getInnerFile(_fileName);
		if(!session.getUser().hasReadPermissions(file)) throw new PermissionDeniedException();
		FileContentsVisitor visitor = new FileContentsVisitor();
		file.accept(visitor);
		_content = visitor.getFileContents();
		
	}
	
	public String result(){
		return _content;
	}

}
	