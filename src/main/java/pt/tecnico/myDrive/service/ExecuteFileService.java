package pt.tecnico.myDrive.service;

import java.util.ArrayList;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.ExecuteFileVisitor;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Visitor;
import pt.tecnico.myDrive.exception.AppExecutionException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class ExecuteFileService extends MyDriveService {

	private MyDrive _drive;
	private long _token;
	private String _path;
	private String[] _args;

	/**
	 * Default Constructor
	 * @throws InvalidFileNameException
	 */
	public ExecuteFileService(long token, String path, String[] args)  {
		_token = token;
		_path = path;
		_args = args;
		_drive = MyDriveService.getMyDrive();
	}

	/**
	 * Implementation of the service
	 * @throws InvalidTokenException
	 * @throws PermissionDeniedException 
	 * @throws UnsupportedOperationException 
	 */
	@Override
	public final void dispatch() throws AppExecutionException, InvalidTokenException, FileNotFoundException, PermissionDeniedException, UnsupportedOperationException{
		
		Session 	session 	= _drive.validateToken(_token);
		User    	user    	= session.getUser();
		Directory	currentDir	= session.getCurrentDirectory();
		
		File targetFile;
		if(_path.charAt(0) == '/'){
			//System.out.println("\u001B[33mGoing for root\u001B[0m");
			targetFile = _drive.getFile(_path,session.getUser());
		}
		else{
			//System.out.println("\u001B[33mGoing for Relative\u001B[0m");
			targetFile = currentDir.getFile(_path, session.getUser());
		}
		Visitor visitor = new ExecuteFileVisitor(_args);
		targetFile.accept(visitor);
	}
}
