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
import pt.tecnico.myDrive.exception.FileExistsException;

import java.util.ArrayList;

public class Directory extends Directory_Base {

	static final Logger log = LogManager.getRootLogger();

	/**
	 * This is the most used constructor is used to create directories
	 */
	public Directory(String name, Integer id, DateTime modification,
	Integer permissions, User owner, Directory father)
	throws FileExistsException {
	    init(name, id, modification, permissions, owner, father);
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
	private Directory(String name, Integer id, DateTime modification, Integer permissions, User owner) {
	        setName(name);
	        setId(id);
	        setModification(modification);
	        setPermissions(permissions);
	        setOwner(owner);
	        setDir(this);
  }

  public static Directory createRootDirectory(String name, Integer id, DateTime modification, Integer permissions, User owner) {
	  		return new Directory(name, id,modification, permissions, owner);
  }
  /**
   * Constructor that is used to import Directory from a XML Element
   *
   * @param XML Element Node
   * @param Directory parent
   * @throws FileExistsException
   * @throws NumberFormatException
   */
  /* TODO ANDRE this was here, probably remove
  public Directory(Element e, Directory parent, User owner) throws NumberFormatException, FileExistsException {
    this(
      e.getAttribute("name").getValue(),
      Integer.parseInt(e.getAttribute("id").getValue()),
      DateTime.parse(e.getAttribute("modification").getValue()),
      Integer.parseInt(e.getAttribute("permissions").getValue()),
      owner,
      parent);
  }*/
<<<<<<< HEAD

  	public Directory(Element xml, User owner, Directory parent) throws FileAlreadyExistsException {
=======
  
  	public Directory(Element xml, User owner, Directory parent) throws FileExistsException {
>>>>>>> 28c27f86a161a8d710451bb29e4757dd5f398561
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
	public void deleteFile() throws NotDirectoryException,
	DirectoryIsNotEmptyException {
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
	
	public void addFile(String path, File file) throws FileExistsException{
		if(path.equals("")){
			this.addChildFile(file);
			return;
		}
		//TODO recursive shit
		
	}
	
}
