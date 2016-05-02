package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import pt.tecnico.myDrive.domain.Application;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

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
		if (session != null) {
			directory = session.getCurrentDirectory();
		} else {
			directory = null;
		}
	}
	
	public ListDirectoryService(long token, String path) throws FileNotFoundException, NotDirectoryException {
		this.token = token;
		myDrive = MyDriveService.getMyDrive();
		session = myDrive.getSessionByToken(token);
		if (session != null) {
			directory = myDrive.getDirectory(path, session.getUser());
		} else {
			directory = null;
		}
	}

	@Override
	public final void dispatch() throws InvalidTokenException, PermissionDeniedException {
		myDrive.validateToken(token);

		if (!session.getUser().hasReadPermissions(directory))
			throw new PermissionDeniedException();

		list = new ArrayList<List<String>>();
		Set<File> files = directory.getFilesSet();

		List<String> currentDir = setDirectory(".");
		List<String> parentDir = setDirectory("..");

		list.add(currentDir);
		list.add(parentDir);

		for (File file : files) {
			boolean link = false;
			List<String> thisResult = new ArrayList<String>();
			if (file instanceof Application) {
				thisResult.add("Application");
			} else if (file instanceof Link) {
				thisResult.add("Link");
				link = true;
			} else if (file instanceof PlainFile) {
				thisResult.add("Plain File");
			} else if (file instanceof Directory) {
				thisResult.add("Directory");
			}
			thisResult.add(file.getPermissions());

			if (file instanceof PlainFile) {
				thisResult.add(String.valueOf(((PlainFile) file).getContent().length()));
			} else if (file instanceof Directory) {
				Set<File> contents = ((Directory) file).getFilesSet();
				thisResult.add(String.valueOf(contents.size() + 2));
			} else {
				thisResult.add("0");
			}

			thisResult.add(file.getOwner().getUsername());
			thisResult.add(String.valueOf(file.getId()));
			thisResult.add(file.getModification().toString());
			if (link)
				thisResult.add(file.getName() + " -> " + ((PlainFile) file).getContent());
			else
				thisResult.add(file.getName());

			list.add(thisResult);
		}

		Collections.sort(list, new Comparator<List<String>>() {
			public int compare(List<String> o1, List<String> o2) {
				return o1.get(6).compareTo(o2.get(6));
			}
		});
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
