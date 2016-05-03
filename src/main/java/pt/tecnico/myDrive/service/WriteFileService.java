package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Application;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.ExecuteFileVisitor;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Visitor;
import pt.tecnico.myDrive.domain.WriteFileVisitor;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.NotPlainFileException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class WriteFileService extends MyDriveService {

	private long token;
	private String path;
	private String content;
	private MyDrive myDrive;


	/**
	 * Default Constructor
	 * @throws PermissionDeniedException 
	 * @throws FileNotFoundException 
	 */
	public WriteFileService(long token, String path, String content) {
		this.token = token;
		this.path = path;
		this.content = content;
	}
	
	/**
	 * Implementation of the service
	 * @throws PermissionDeniedException 
	 * @throws NotPlainFileException 
	 * @throws InvalidTokenException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedOperationException 
	 */ 
	@Override
	public final void dispatch() throws PermissionDeniedException, InvalidTokenException, FileNotFoundException, UnsupportedOperationException {
		
		this.myDrive = MyDriveService.getMyDrive();
		Session 	session 	= myDrive.validateToken(token);
		User    	user    	= session.getUser();
		Directory	currentDir	= session.getCurrentDirectory();
		
		File targetFile;
		if(path.charAt(0) == '/'){
			//System.out.println("\u001B[33mGoing for root\u001B[0m");
			targetFile = myDrive.getFile(path, user);
			System.out.println("\u001B[31m PATH TO FILE:" + targetFile.getPath() + "\u001B[0m)");
		}
		else{
			//System.out.println("\u001B[33mGoing for Relative\u001B[0m");
			targetFile = currentDir.getFile(path, user);
		}
		Visitor visitor = new WriteFileVisitor(content, user);
		targetFile.accept(visitor);
	}
		
}
 