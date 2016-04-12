package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pt.tecnico.myDrive.domain.Application;
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
			if (file instanceof Application) {
				thisResult.add("Application");
				thisResult.add(file.getPermissions());

				if (file instanceof Application) {
					thisResult.add(String.valueOf(((Application) file).getContent().length()));
				} else {
					thisResult.add("0");
				}
				thisResult.add(file.getOwner().getUsername());
				thisResult.add(String.valueOf(file.getId()));
				thisResult.add(file.getModification().toString());
				thisResult.add(file.getName());
				list.add(thisResult);
			}
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
