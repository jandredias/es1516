package pt.tecnico.myDrive.domain;

import java.util.ArrayList;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class File extends File_Base {

	protected File() { /* for deriver classes */ }

	protected void init (String name, User owner) throws FileExistsException,
			InvalidFileNameException {

		String permissions = owner.getPermissions();
		init(name, MyDrive.getNewFileId(), new DateTime(), permissions, owner);
	}

	/**
	 * Constructor XML
	 * @param xml
	 * @param owner
	 * @throws FileExistsException
	 * @throws InvalidFileNameException
	 */
	public File(Element xml) {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		DateTime modification = new DateTime();
		if(xml.getChild("modification") != null)
			modification = DateTime.parse(xml.getChild("modification").getValue());

		String permissions = "rwxd----";
		if(xml.getChild("permissions") != null)
			permissions = xml.getChild("permissions").getValue();
		try{
		 init(xml.getChild("name").getValue(), MyDrive.getNewFileId(), modification,
	 			permissions, null);
		}catch(FileExistsException | InvalidFileNameException e){
			System.out.println("This won't happen");
		}
	}

	/**
	 * Real Constructor of File that is used by every other constructor
	 * Note that the parent directory is not set, the parent directory is the
	 *  one that needs to add its child
	 *
	 * @param name
	 * @param id
	 * @param modification
	 * @param permissions
	 * @param owner
	 * @throws FileExistsException
	 * @throws InvalidFileNameException
	 */
	protected void init(String name, Integer id , DateTime modification,
			String permissions, User owner) throws FileExistsException,
				InvalidFileNameException{
		
		if( name.contains("/") || name.contains("\0")){
			this.deleteDomainObject();
			throw new InvalidFileNameException(name);
		}
		if( name.equals(".") || name.equals("..") ) {
			this.deleteDomainObject();
			throw new FileExistsException(name);
		}
		setName(name);
		setId(id);
		setModification(modification);
		setPermissions(permissions);
		setOwner(owner);
	}

	/**
	 * Removes file from FenixFramework Database
	 *
	 * @throws DirectoryIsNotEmptyException
	 */
	public void delete(User user)
			throws PermissionDeniedException {

		if(!user.hasWritePermissions(this.getDir())) throw new PermissionDeniedException("write on father dir");
		if(!user.hasDeletePermissions(this)) throw new PermissionDeniedException("delete on file");
		this.setDir(null);
		this.setOwner(null);

		deleteDomainObject();
	}


	/**
	 * Throws exception when File cannot be a parent File
	 *
	 * @throws NotDirectoryException
	 */
	public void isParentable() throws NotDirectoryException{
		throw new NotDirectoryException();
	}

	public File getFile(String fileName) throws NotDirectoryException, FileNotFoundException {
		throw new NotDirectoryException(fileName);
	}

	/**
	 * @return String that is equal to File Path
	 */
	public String getPath() {
		String myName = getName();
		if (myName.equals("/"))
			return myName;
		else {
			Directory fatherDir = getDir();
			if(fatherDir.getPath().equals("/"))
				return fatherDir.getPath() + myName;
			return fatherDir.getPath() + "/" + myName;
		}
	}

	/**
	 * @return String that is equal to File's Directory Path
	 */
	public String getFatherPath() {
		Directory fatherDir = getDir();
		return fatherDir.getPath();
	}


	public ArrayList<Element> xmlExport() {
		ArrayList<Element> array = new ArrayList<Element>();
		Element element = new Element("file");
		element.setAttribute("id",getId().toString());

		Element pathElement = new Element("path");
		pathElement.addContent(getFatherPath());
		element.addContent(pathElement);

		Element ownerElement = new Element("owner");
		ownerElement.addContent(getOwner().getUsername());
		element.addContent(ownerElement);

		Element nameElement = new Element("name");
		nameElement.addContent(getName());
		element.addContent(nameElement);

		Element modificationElement = new Element("modification");
		modificationElement.addContent(getModification().toString());
		element.addContent(modificationElement);

		Element permissionsElement = new Element("permissions");
		permissionsElement.addContent(getPermissions());
		element.addContent(permissionsElement);
		array.add(element);
		return array;
	}


	public void accept(Visitor visitor) throws UnsupportedOperationException {
		visitor.visitFile(this);
	}

}
