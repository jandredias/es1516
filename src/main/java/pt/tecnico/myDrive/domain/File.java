package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import org.joda.time.DateTime;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.FileNotFoundException;

public class File extends File_Base {
    

	protected File() {
		/* for deriver classes */
	}
    
    public File(String name, Integer id, DateTime modification, Integer permissions, pt.tecnico.myDrive.domain.User owner){
    	init(name, id, modification, permissions, owner);
    }

    protected void init(String name, Integer id, DateTime modification, Integer permissions, pt.tecnico.myDrive.domain.User owner){
    	setName(name);
    	setId(id);
    	setModification(modification);
    	setPermissions(permissions);
    	setOwner(owner);
    }

    public File getFile(String fileName) throws NotDirectoryException, FileNotFoundException {
		throw new NotDirectoryException();
	}
    
    public String getPath() {
    	String myName = getName();
    	if ( ! myName.equals("/") )
    		// when the strings match
    		return myName;
    	else {
    		Directory fatherDir = getDir();
    		return fatherDir.getPath() + myName;
    	}
    }
    
    public void deleteFile() throws NotDirectoryException, DirectoryIsNotEmptyException {
    	Directory parentDirectory = getDir();
    	parentDirectory.removeFiles(this);
    }
    
    public Element xmlExport() {
    	Element element = new Element("file");
    	
    	element.setAttribute("name",getName());
    	element.setAttribute("id",getId().toString());
    	element.setAttribute("modification",getModification().toString());//TODO
    	element.setAttribute("permissions",Integer.toString(getPermissions()));
    	element.setAttribute("father-dir",getDir().getPath());
    	
    	return element;
    }

	public void accept(Visitor visitor) throws UnsupportedOperationException {
		visitor.visitFile(this);
	}
}
