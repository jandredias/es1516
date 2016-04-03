package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.UnknowFileTypeException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;

public class CreateFileService extends MyDriveService {

	private MyDrive _drive;
	private String  _username = "";
	private String  _path     = "";
	private String  _content;
	private String  _fileType;
	private String  _fileName;
	
	/**
	 * Default Constructor
	 */
	public CreateFileService(long token, String fileName, String fileType, 
			String content ) {
		
		_drive = MyDrive.getInstance();
		
		//TODO:FIXME:XXX _username = Session.getUsername(token);
		//TODO:FIXME:XXX _path 	  = Session.getCurrentDirectory(token);
		
		_content = content;
		_fileType = fileType;
		_fileName = fileName;
	}
	
	
	/**
	 * Implementation of the service
	 * @throws FileNotFoundException 
	 * @throws InvalidFileNameException 
	 * @throws FileExistsException 
	 * @throws UnknowFileTypeException 
	 * @throws UserDoesNotExistsException 
	 */
	@Override
	public final void dispatch() throws FileExistsException, 
				InvalidFileNameException, FileNotFoundException, UnknowFileTypeException, UserDoesNotExistsException{
		
		switch(_fileType) {
			case "app":
				_drive.addApplication(_path, _fileName, getUser(_username), _content);
				break;
			case "link":
				_drive.addLink(_path, _fileName, getUser(_username), _content);
				break;
			case "plainfile":
				_drive.addPlainFile(_path, _fileName, getUser(_username), _content);
				break;
			case "dir":
				_drive.addDirectory(_path, _fileName, getUser(_username));
				break;
			default:
				log.error("Unknown Type File");
				throw new UnknowFileTypeException(_fileType);
		}
	}
}
