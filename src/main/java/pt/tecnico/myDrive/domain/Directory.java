package pt.tecnico.myDrive.domain;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class Directory extends Directory_Base {

	static final Logger log = LogManager.getRootLogger();
	private static final String DEFAULT_ROOT_PERMISSION = "rwxdr-x-";

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

	protected static Directory createRootDirectory(User owner) {
		return new Directory("/", new DateTime(), owner.getPermissions(), owner);
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
			throws UnsupportedOperationException, PermissionDeniedException {
		visitor.visitDirectory(this);
	}

	@Override
	public void delete(User deleter)
			throws PermissionDeniedException {

		if(this.getName().equals("/")){
			throw new PermissionDeniedException("Cannot Delete Root Dir");
		}
		
		for(File f : getFilesSet())
			f.delete(deleter);

		for (User user : getOwnerHomeSet()) {
			log.info("User " + user.getName() + " changed his home directory to " + this.getDir().getPath()  );
			user.setUsersHome(this.getDir());
		}
		super.delete(deleter);
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
		try {
			return getInnerFile(fileName, MyDrive.getInstance().getRootUser());
		} catch (PermissionDeniedException e) {
			//Root always have permissions
			return null; //Compilation Required
		}
	}
	public File getInnerFile(String fileName, User user)	throws FileNotFoundException, PermissionDeniedException {

		for(File file: getFilesSet())
			if(file.getName().equals(fileName)){
				if(file.getClass() == Link.class){
					Link link = ( Link ) file;
					file = link.getFile(user);
				}
				return file;
			}
		if (fileName.equals(".")) {
			return this;
		}
		else if (fileName.equals("..")) {
			return this.getDir();
		}
		throw new FileNotFoundException("File: " + fileName + " not Found");
	}

	public Directory getDirectory(String path, User user)throws FileNotFoundException, PermissionDeniedException {
		
		File file = getFile(path, user);
		if(file.getClass() == Link.class) {
			Link link = ( Link ) file;
			file = link.getFile(user);
		}
		if (file.getClass() == Directory.class){
			return (Directory) file;
		} else
			throw new FileNotFoundException("Directory: " + path + " not Found");
	}

	/* FIXME */
	public Directory getDirectory(String path)throws FileNotFoundException {
		try {
			return getDirectory(path, MyDrive.getInstance().getRootUser());
		} catch (PermissionDeniedException e) {
			//Root always have permissions
			return null; //Compilation Required
		}
	}
	
	/** FIXME*/
	public File getFile(String path) throws FileNotFoundException {
		try {
			return getFile(path, MyDrive.getInstance().getRootUser());
		} catch (PermissionDeniedException e) {
			//Root always have permissions
			return null; //Compilation Required
		}
	}
	
	public File getFile(String path, User user) throws FileNotFoundException, PermissionDeniedException {
		if(path.contains("//"))
			throw new FileNotFoundException();
			
		if( path.equals("") )
			return this;
		
		if(!user.hasExecutePermissions(this)) throw new PermissionDeniedException("Execute Permissions On dir " + this.getName());
		ArrayList<String> pieces = MyDrive.pathToArray(path);

		if (pieces.size() == 1) {
			return getInnerFile(pieces.get(0),user);
		}

		Directory nextDir = getDirectory(pieces.get(0),user);
		pieces.remove(0);

		String newPath = MyDrive.arrayToString(pieces);
		return nextDir.getFile(newPath, user);
	}
	
	/**
	 * Remove a file if it's a child or call a child element to do it for him
	 *
	 * @param String
	 * @throws FileNotFoundException
	 * @throws DirectoryIsNotEmptyException
	 */
	public void removeFile(String path, User user) throws FileNotFoundException, PermissionDeniedException{

		
		ArrayList<String> pieces = MyDrive.pathToArray(path);
		if (pieces.size() == 1) {
			File fileToBeDeleted = this.getInnerFile(pieces.get(0));
			if (fileToBeDeleted == null)
				throw new FileNotFoundException(pieces.get(0));
			fileToBeDeleted.delete(user);

		} else {
			Directory nextDir = getDirectory(pieces.get(0));
			pieces.remove(0);

			String newPath = MyDrive.arrayToString(pieces);
			nextDir.removeFile(newPath, user);
		}
	}
	
	public String toString() {
		return getPath();
	}

	/**
	 * 	 * Adds a file if it's a child or call a child element to do it for him
	 * @param String
	 * @param File
	 * @param creator
	 * @throws FileExistsException
	 * @throws FileNotFoundException
	 * @throws PermissionDeniedException
	 */
	public void addFile(String path, File file, User creator) throws FileNotFoundException,
	FileExistsException, PermissionDeniedException {
		if(path.equals("") || path.equals("/")){
			if(creator.hasWritePermissions(this)){
				if(hasFile(file.getName()))
					throw new  FileExistsException(file.getName());
				else
					addFiles(file);
				return;
			}
			else{
				throw new PermissionDeniedException("Write on Directory: " + file.getName());
			}
		} else {
			Directory nextDir = null;
			nextDir = getDirectory(path);
			nextDir.addFile("", file, creator);
		}
	}
}
