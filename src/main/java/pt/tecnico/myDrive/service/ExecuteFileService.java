package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;

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
		_drive = MyDriveService.getMyDrive();
		_token = token;
		_path = path;
		_args = args;
	}

	/**
	 * Implementation of the service
	 * @throws InvalidTokenException
	 */
	public final void dispatch(){
		/* PLEASE PUT LOGIC OF SERVICE ON THE DOMAIN */
	}
}
