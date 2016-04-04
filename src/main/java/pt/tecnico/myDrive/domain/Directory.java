package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileExistsException;

import java.util.ArrayList;

public class Directory extends Directory_Base {

	static final Logger log = LogManager.getRootLogger();
	private static final String DEFAULT_ROOT_PERMISSION = "rwxd----";

	/**
	 * This is the most used constructor is used to create directories
	 * @throws InvalidFileNameException
	 */
	public Directory(String name, User owner)
			throws FileExistsException, InvalidFileNameException {

		init(name, owner);
	}

	/**
	 * This constructor is used by createRootDir when called by Manager
	 * to create the root directory
	 *
	 * @param String name
	 * @param Integer id
	 * @param Datetime modification date
	 * @param Integer permissions
	 * @param User owner
	 */
	private Directory(String name, DateTime modification, String permissions,
			User owner) {

		//FIXME looks disgusting..
		setName(name);
		setId(MyDrive.getNewFileId());
		setModification(modification);
		setPermissions(permissions);
		setOwner(owner);
		setDir(this);
	}

	public static Directory createRootDirectory(User owner) {
		return new Directory("/", new DateTime(), DEFAULT_ROOT_PERMISSION, owner);
	}

	public Directory(Element xml) {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
	}

	/**
	 * Throws exception when File cannot be a parent File
	 *
	 * @throws NotDirectoryException
	 */
	/* FIXME Really Needed? Don't Forget File Class */
	public void isParentable() throws NotDirectoryException{}

	public void accept(Visitor visitor)
			throws UnsupportedOperationException {
		visitor.visitDirectory(this);
	}

	@Override
	public void delete() throws DirectoryIsNotEmptyException {

		if(getFilesSet().isEmpty()){
			for (User user : getOwnerHomeSet()) {
				Directory newHome = this.getDir();
				log.info("User " + user.getName() + " changed his home directory to " + newHome.getPath()  );
				user.setUsersHome(newHome);
			}
			super.delete();
		}
		else throw new DirectoryIsNotEmptyException();
	}

	public ArrayList<Element> xmlExport() {

		ArrayList<Element> array = super.xmlExport();
		array.get(0).setName("dir");

		for(File file : getFilesSet())
			if(!file.getName().equals("/"))
				for(Element el : file.xmlExport())
					array.add(el);
		return array;

	}

	/**
	 * Method that returns true if a directory as a file with the name: fileName
	 *
	 * @param fileName
	 * @return
	 */
	public boolean hasFile(String fileName)  {

		try {
			getInnerFile(fileName);
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}

	public File getInnerFile(String fileName)	throws FileNotFoundException {

		for(File file: getFilesSet())
			if(file.getName().equals(fileName))
				return file;
		if (fileName.equals(".")) {
			return this;
		}
		else if (fileName.equals("..")) {
			return this.getDir();
		}
		throw new FileNotFoundException("File: " + fileName + " not Found");
	}


	public Directory getDirectory(String path)throws FileNotFoundException {

		File directory = getFile(path);
		if (directory.getClass() == Directory.class)
			return (Directory) directory;
		else
			throw new FileNotFoundException("Directory: " + path + " not Found");
	}



	public File getFile(String path) throws FileNotFoundException {

		if( path.equals("") )
			return this;

		ArrayList<String> pieces = MyDrive.pathToArray(path);

		if (pieces.size() == 1) {
			return getInnerFile(pieces.get(0));
		}

		Directory nextDir = getDirectory(pieces.get(0));
		pieces.remove(0);

		String newPath = MyDrive.arrayToString(pieces);
		return nextDir.getFile(newPath);
	}
	/**
	 * Remove a file if it's a child or call a child element to do it for him
	 *
	 * @param String
	 * @throws FileNotFoundException
	 * @throws DirectoryIsNotEmptyException
	 */
	public void removeFile(String path) throws FileNotFoundException,
	DirectoryIsNotEmptyException{

		ArrayList<String> pieces = MyDrive.pathToArray(path);

		if (pieces.size() == 1) {
			File fileToBeDeleted = this.getInnerFile(pieces.get(0));
			if (fileToBeDeleted == null)
				throw new FileNotFoundException(pieces.get(0));
			fileToBeDeleted.delete();

		} else {
			Directory nextDir = getDirectory(pieces.get(0));
			pieces.remove(0);

			String newPath = MyDrive.arrayToString(pieces);
			nextDir.removeFile(newPath);
		}
	}

	/**
	 * Adds a file if it's a child or call a child element to do it for him
	 *
	 * @param String
	 * @param File
	 * @throws FileExistsException
	 * @throws FileNotFoundException
	 */
	public void addFile(String path, File file) throws FileNotFoundException,
	FileExistsException {

		if(path.equals("") || path.equals("/")){
			if(hasFile(file.getName()))
				throw new  FileExistsException(file.getName());
			else
				addFiles(file);
			return;
		} else {
			Directory nextDir = null;
			nextDir = getDirectory(path);
			nextDir.addFile("", file);
		}
	}
}
