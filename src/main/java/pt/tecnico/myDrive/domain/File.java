package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import java.util.ArrayList;

public class File extends File_Base {


  protected File() { /* for deriver classes */ }

  public File(String name, Integer id, DateTime modification, Integer permissions, User owner, Directory parent) throws FileAlreadyExistsException{
    init(name, id, modification, permissions, owner, parent);
  }

  public File(Element xml, User owner, Directory parent) throws FileAlreadyExistsException{
	  this.xmlImport(xml, owner, parent);
  }

  protected void xmlImport(Element xml, User owner, Directory parent) throws FileAlreadyExistsException {
    init(
      xml.getChild("name").getValue(),
      Integer.parseInt(xml.getAttribute("id").getValue()),
      DateTime.parse(xml.getChild("modification").getValue()),
      Integer.parseInt(xml.getChild("permissions").getValue()),
      owner,
      parent
      );
  }

/**
   * Throws exception when File cannot be a parent File
   *
   * @throws NotDirectoryException
   */
  public void isParentable() throws NotDirectoryException{
    throw new NotDirectoryException();
  }

  protected void init(String name, Integer id, DateTime modification,
    Integer permissions, User owner, Directory parent) throws FileAlreadyExistsException{


	setName(name);
    setId(id);
    setModification(modification);
    setPermissions(permissions);
    setOwner(owner);

    parent.addChildFile(this);

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

  public void deleteFile() throws NotDirectoryException, DirectoryIsNotEmptyException {

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
 	permissionsElement.addContent(Integer.toString(getPermissions()));
 	element.addContent(permissionsElement);

    array.add(element);
    return array;
  }


  public void accept(Visitor visitor) throws UnsupportedOperationException {
    visitor.visitFile(this);
  }
}
