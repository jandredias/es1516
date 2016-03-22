package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;

import java.util.ArrayList;

public class File extends File_Base {

	protected File() { /* for deriver classes */ }
	
/*
	public File(String name, User owner) throws FileExistsException,
			InvalidFileNameException {
		
		int permissions = owner.getPermissions();
		init(name, MyDrive.getNewFileId(), new DateTime(), permissions,owner);
	}
*/
	
	protected void init (String name, User owner) throws FileExistsException,
			InvalidFileNameException {

		int permissions = owner.getPermissions();
		init(name, MyDrive.getNewFileId(), new DateTime(), permissions,owner);
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
		
		/* TODO:FIXME:XXX ANDRE U HAD 1 JOB
			Integer id = Integer.parseInt(xml.getAttribute("id").getValue());
			DateTime modification = new DateTime();
			Integer permissions = 1;
	
			if(xml.getChild("modification") != null)
				modification = DateTime.parse(xml.getChild("modification").getValue());
	
			if(xml.getChild("permissions") != null)
				permissions = Integer.parseInt(xml.getChild("permissions").getValue());
			init(xml.getChild("name").getValue(),
					id, modification, permissions, owner, parent);
		 */
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
			Integer permissions, User owner) throws FileExistsException, 
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
	public void delete() throws DirectoryIsNotEmptyException {
		
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
		permissionsElement.addContent(MyDrive.permissions(getPermissions()));
		element.addContent(permissionsElement);

		array.add(element);
		return array;
	}


	public void accept(Visitor visitor) throws UnsupportedOperationException {
		visitor.visitFile(this);
	}
}
