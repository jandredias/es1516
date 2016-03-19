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
  
  public File(String name, DateTime modification, Integer permissions, User owner, Directory parent) throws FileExistsException, InvalidFileNameException{
    init(name, modification, permissions, owner, parent);
  }

  public File(Element xml, User owner, Directory parent) throws FileExistsException, InvalidFileNameException{
	  this.xmlImport(xml, owner, parent);
  }

  protected void xmlImport(Element xml, User owner, Directory parent) throws FileExistsException, InvalidFileNameException {
    Integer id = Integer.parseInt(xml.getAttribute("id").getValue());
    DateTime modification = new DateTime();
    Integer permissions = 1;

    if(xml.getChild("modification") != null)
      modification = DateTime.parse(xml.getChild("modification").getValue());

    if(xml.getChild("permissions") != null)
      permissions = Integer.parseInt(xml.getChild("permissions").getValue());
    init(xml.getChild("name").getValue(),
      id, modification, permissions, owner, parent);
  }

  protected void init(String name, DateTime modification,
		    Integer permissions, User owner, Directory parent) throws FileExistsException, InvalidFileNameException{

	  init(name, MyDrive.getNewFileId() ,modification,permissions, owner, parent);
  }


	protected void init(String name, Integer id , DateTime modification,
		  Integer permissions, User owner, Directory parent) throws FileExistsException, InvalidFileNameException{
	if(name.contains("/") || name.contains("\0")){
		throw new InvalidFileNameException(name);
	}

	setName(name);

	try {
		parent.addFile("",this);
	} catch (FileNotFoundException e) {
		// Impossible condition
	} catch (FileExistsException e ) {
		this.deleteDomainObject();
		throw e;
	}
	
	if( name.equals(".") || name.equals("..") ) {
		this.deleteDomainObject();
		throw new FileExistsException(name);
	}

    setId(id);
    setModification(modification);
    setPermissions(permissions);
    setOwner(owner);


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

  public String getPath() {
	  String myName = getName();
	  if (myName.equals("/"))
		  return myName;
	  else {
	      Directory fatherDir = getDir();
	      if(fatherDir.getPath().equals("/")) return fatherDir.getPath() + myName;
	      return fatherDir.getPath() + "/" + myName;
	  }
  }

  public String getFatherPath() {
      Directory fatherDir = getDir();
      return fatherDir.getPath();
  }

  public void deleteFile() throws DirectoryIsNotEmptyException {

    this.setDir(null);
    this.setOwner(null);

    deleteDomainObject();
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
