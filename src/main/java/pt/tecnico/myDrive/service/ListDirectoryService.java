package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.List;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.InvalidTokenException;

public class ListDirectoryService extends MyDriveService {

	private long token;
	private List<List<String>> list;
	private MyDrive myDrive;
	private Session session;
	private Directory directory;

	/**
	 * Default Constructor
	 * 
	 * @throws InvalidTokenException
	 */
	public ListDirectoryService(long token) {
		this.token = token;
		myDrive = MyDriveService.getMyDrive();
		session = myDrive.getSessionByToken(token);
		directory = session.getCurrentDirectory();
	}

	@Override
	public final void dispatch() throws InvalidTokenException {
		myDrive.validateToken(token);
		list = new ArrayList<List<String>>();
		directory.getFilesSet();
	}

	public final List<List<String>> result() {
		return list;
	}
}
