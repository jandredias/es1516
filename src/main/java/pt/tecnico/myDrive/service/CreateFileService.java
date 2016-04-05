package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UnknowFileTypeException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;

public class CreateFileService extends MyDriveService {

	private MyDrive _drive;
	//private String  _username = "";
	//private String  _path     = "";
	private String  _content;
	private String  _fileType;
	private String  _fileName;
	private long 	_token;


	/**
	 * Default Constructor
	 */
	public CreateFileService(long token, String fileName, String fileType, 
			String content ) {

		_drive = MyDriveService.getMyDrive();
		_content = content;
		_fileType = fileType;
		_fileName = fileName;
		_token = token;
	}


	/**
	 * Implementation of the service
	 * @throws FileNotFoundException 
	 * @throws InvalidFileNameException 
	 * @throws FileExistsException 
	 * @throws UnknowFileTypeException 
	 * @throws UserDoesNotExistsException 
	 * @throws InvalidTokenException 
	 * @throws PermissionDeniedException 
	 */
	@Override
	public final void dispatch() throws FileExistsException, 
			InvalidFileNameException, FileNotFoundException, UnknowFileTypeException, UserDoesNotExistsException, InvalidTokenException, PermissionDeniedException{

		Session session = _drive.validateToken(_token);
		User    user    = session.getUser();
		String _path    = session.getCurrentDirectory().getPath();

		switch(_fileType) {
		case "app":
			_drive.addApplication(_path, _fileName, user, _content);
			break;
		case "link":
			_drive.addLink(_path, _fileName, user, _content);
			break;
		case "plainfile":
			_drive.addPlainFile(_path, _fileName, user, _content);
			break;
		case "dir":
			_drive.addDirectory(_path, _fileName,user);
			break;
		default:
			log.error("Unknown Type File");
			throw new UnknowFileTypeException(_fileType);
		}
	}
}
