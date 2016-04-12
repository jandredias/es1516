package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
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
		Set<File> files = directory.getFilesSet();

		List<String> currentDir = setDirectory(".");
		List<String> parentDir = setDirectory("..");

		list.add(currentDir);
		list.add(parentDir);

		for (File file : files) {
			List<String> thisResult = new ArrayList<String>();
			// don't care right now
		}
	}

	public final List<List<String>> result() {
		return list;
	}

	private List<String> setDirectory(String name) {
		List<String> directory = new ArrayList<String>();
		directory.add("Directory");
		directory.add(" ");
		directory.add(" ");
		directory.add(" ");
		directory.add(" ");
		directory.add(" ");
		directory.add(name);
		return directory;

	}
}
