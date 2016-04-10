package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;

public class DeleteFileService extends MyDriveService {

	private MyDrive _drive;
	private long _token;
	private String _fileName;
	
	/**
	 * Default Constructor
	 */
	public DeleteFileService(long token, String fileName) {
		_drive = MyDriveService.getMyDrive();
		_token = token;
		_fileName = "./" + fileName;
	}
	
	/**
	 * Implementation of the service
	 * @throws InvalidTokenException 
	 */
	public final void dispatch() throws InvalidTokenException,
			FileNotFoundException, DirectoryIsNotEmptyException, MyDriveException{
		
		Session session = _drive.validateToken(_token);
		
		Directory currentDir = session.getCurrentDirectory();
		File targetFile = currentDir.getFile(_fileName);
		
		User user = session.getUser();
		if(user.hasDeletePermissions(targetFile)){
			targetFile.delete();
		}
		
		/*if(targetFile instanceof Directory){
			if( ((Directory)targetFile).getFilesSet().isEmpty()){
				targetFile.delete();
			}
			else{
				for (File f : ((Directory)targetFile).getFilesSet()) {
					DeleteFileService service = new DeleteFileService(_token, f.getName());
					service.execute();
				}
			}
		}
		else{
			targetFile.delete();
		}*/
	}
}
