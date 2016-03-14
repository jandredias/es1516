package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileExistsException;

import java.util.ArrayList;
import java.util.Arrays;

public class Directory extends Directory_Base {

	static final Logger log = LogManager.getRootLogger();

	/**
	 * This is the most used constructor is used to create directories
	 */
	public Directory(String name, DateTime modification,
									Integer permissions, User owner, Directory father)
									throws FileExistsException {
	    init(name, modification, permissions, owner, father);
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
	private Directory(String name, DateTime modification, Integer permissions, User owner) {
	        setName(name);
	        setId(MyDrive.getNewFileId());
	        setModification(modification);
	        setPermissions(permissions);
	        setOwner(owner);
	        setDir(this);
	  }

	  public static Directory createRootDirectory(User owner) {
		  		return new Directory("/", new DateTime(),11111010, owner);
	  }


  	public Directory(Element xml, User owner, Directory parent) throws FileExistsException{
		this.xmlImport(xml, owner, parent);
	}

	protected void xmlImport(Element xml, User owner, Directory parent) throws FileExistsException {
		super.xmlImport(xml, owner, parent);
	}

	/**
	* Throws exception when File cannot be a parent File
	*
	* @throws NotDirectoryException
	*/
	public void isParentable() throws NotDirectoryException{}

	public void accept(Visitor visitor)
	throws UnsupportedOperationException {
		visitor.visitDirectory(this);
	}

	public File getFile(String fileName)
	throws FileNotFoundException {
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

	public Directory getDirectory(String fileName)
	throws FileNotFoundException {
		File directory = getFile(fileName);
		if (directory.getClass() == Directory.class)
			return (Directory) directory;
		else
			throw new FileNotFoundException("Directory: " + fileName + " not Found");
	}

	@Override
	public void deleteFile() throws DirectoryIsNotEmptyException {
		if(getFilesSet().isEmpty()){
			for (User user : getOwnerHomeSet()) {
				Directory newHome = this.getDir();
				log.info("User " + user.getName() + " changed his home directory to " + newHome.getPath()  );
				user.setUsersHome(newHome);
			}
			super.deleteFile();
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

	public boolean hasFile(String fileName)  {
		try {
		  getFile(fileName);
		} catch (FileNotFoundException e) {
		  return false;
		}
		return true;
	}

	/**
	* Checks if file exists on Files set and Adds it
	*
	* @param File
	* @throws FileExistsException
	*/
	public void addChildFile(File f) throws FileExistsException {
		  if(hasFile(f.getName()))
			  throw new  FileExistsException(f.getName());
		  else
			  addFiles(f);
	}


  /**
   * Remove a file if it's a child or call a child element to do it for him
   *
   * @param String
   * @throws FileNotFoundException
 * @throws DirectoryIsNotEmptyException
   */
  public void removeFile(String path) throws FileNotFoundException, DirectoryIsNotEmptyException{
  		ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(path.split("/")));

	    //Removing empty String due to / in first position
		if (pieces.size() > 0 && pieces.get(0).equals(""))
	    	pieces.remove(0);

		if (pieces.size() == 1) {
			File fileToBeDeleted = this.getFile(pieces.get(0));
			if (fileToBeDeleted == null)
				throw new FileNotFoundException(pieces.get(0));
			fileToBeDeleted.deleteFile();

		} else {
			Directory nextDir = getDirectory(pieces.get(0));
			pieces.remove(0);

		    String newPath = "";

		    for (String s : pieces)
		    	newPath += (s + "/");

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
	public void addFile(String path, File file) throws FileExistsException, FileNotFoundException{

		if(path.equals("")){
			this.addChildFile(file);
			return;
		}
		else {
			ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(path.split("/")));

		    //Removing empty String due to / in first position
			if (pieces.size() > 0 && pieces.get(0).equals(""))
		    	pieces.remove(0);

      Directory nextDir = null;
      try{
			  nextDir = getDirectory(pieces.get(0));
      }catch(FileNotFoundException e){
        nextDir = new Directory(pieces.get(0), new DateTime(), 11111010, this.getOwner(), this);
        this.addChildFile(nextDir);

      }

      pieces.remove(0);

		    String newPath = "";

		    for (String s : pieces)
		    	newPath += (s + "/");

			nextDir.addFile(newPath, file);
		}
	}

}
