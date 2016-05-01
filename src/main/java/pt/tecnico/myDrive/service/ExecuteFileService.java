package pt.tecnico.myDrive.service;

import java.util.ArrayList;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;

public class ExecuteFileService extends MyDriveService {

	private MyDrive _drive;
	private long _token;
	private String _path;
	private ArrayList<String> _args;

	/**
	 * Default Constructor
	 * @throws InvalidFileNameException
	 */
	public ExecuteFileService(long token, String path, ArrayList<String> args)  {
		_token = token;
		_path = path;
		_args = args;
		_drive = MyDriveService.getMyDrive();
	}

	/**
	 * Implementation of the service
	 * @throws InvalidTokenException
	 */
	@Override
	public final void dispatch() throws InvalidTokenException, FileNotFoundException{
		/* PLEASE PUT LOGIC OF SERVICE ON THE DOMAIN */
		throw new RuntimeException("\u001B[1;31mFIXME: ExecuteFileService: NOT IMPLEMENTED\u001B[0m");
	}
}
